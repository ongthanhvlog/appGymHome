const { onSchedule } = require("firebase-functions/v2/scheduler");
const { onRequest } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2");
const admin = require("firebase-admin");
const axios = require("axios");
const cheerio = require("cheerio");
admin.initializeApp();
setGlobalOptions({ region: "us-central1" });
let tuKhoaUuTienCao = [];
let tuKhoaUuTienThap = [];
let tuKhoaLoaiBo = [];
const headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8',
    'Accept-Language': 'vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7',
};
const DOMAIN_NAME_MAP = {
    "baomoi.com": "Báo Mới"
};

function lamSachNoiDung(text) {
    if (!text) return "";
    let cleaned = text;
    cleaned = cleaned.replace(/\\u([0-9a-fA-F]{4})/g, (match, grp) => String.fromCharCode(parseInt(grp, 16)));
    cleaned = cleaned.replace(/\\"/g, '"').replace(/\\n/g, '\n').replace(/\\r/g, '').replace(/\\t/g, '');
    cleaned = cleaned.replace(/<[^>]*>?/gm, '');
    cleaned = cleaned.replace(/[<>]/g, '');
    cleaned = cleaned.replace(/\\/g, '');
    cleaned = cleaned.replace(/\s+/g, ' ').trim();
    return cleaned;
}
function getSourceName(url) {
    if (!url) return "";
    try {
        const hostname = new URL(url).hostname.replace("www.", "");
        for (const [domain, name] of Object.entries(DOMAIN_NAME_MAP)) {
            if (hostname.includes(domain)) return name;
        }
        const parts = hostname.split(".");
        let name = parts.length > 1 ? parts[parts.length - 2] : parts[0];
        return name.charAt(0).toUpperCase() + name.slice(1);
    } catch (_) { return ""; }
}

function tachNoiDungBaiViet(html) {
    const items = [];
    let originalUrl = "";
    let linkLogo = "";
    const image = new Set();

    const urlMatch = html.match(/"urlOrigin"\s*:\s*"(.*?)"/);
    if (urlMatch) originalUrl = urlMatch[1].replace(/\\/g, '');

    const pngRegex = /https:\/\/photo-baomoi\.bmcdn\.me\/[a-zA-Z0-9_\/]+\.png/gi;
    let pngMatch;
    while ((pngMatch = pngRegex.exec(html)) !== null) {
        const url = pngMatch[0].replace(/\\/g, '');
        if (!linkLogo) linkLogo = url;
    }

    const regexBlockNoiDung = /\{"type":\s*"(text|image)".*?"content":\s*"(.*?)(?<!\\)"/gi;
    let match;
    const danhSachBlock = [];
    while ((match = regexBlockNoiDung.exec(html)) !== null) {
        danhSachBlock.push({
            type: match[1],
            content: match[2],
            index: match.index
        });
    }

    for (const block of danhSachBlock) {
        if (block.type === "text") {
            const cleaned = lamSachNoiDung(block.content);
            if (cleaned.length > 15 && !cleaned.includes("http")) {
                items.push({ type: "text", value: cleaned });
            }
        } else if (block.type === "image") {
            const url = block.content.replace(/\\/g, '');
            if (url.startsWith("http") && (url.includes("w700") || url.includes("w400") || url.includes("w960") || url.includes("w1200"))) {
                const filename = url.substring(url.lastIndexOf('/') + 1);
                if (!image.has(filename)) {
                    image.add(filename);
                    items.push({ type: "image", value: url });
                }
            }
        }
    }
    return { items, originalUrl, linkLogo };
}

async function dongBoBaiViet() {
    const db = admin.firestore();
    try {
        const keywordDoc = await db.collection("TuKhoa").doc("config").get();
        if (keywordDoc.exists) {
            const data = keywordDoc.data();
            tuKhoaUuTienCao = data.TuKhoaUuTienCao || [];
            tuKhoaUuTienThap = data.TuKhoaUuTienThap || [];
            tuKhoaLoaiBo = data.TuKhoaLoaiBo || [];
        }
    } catch (e) {}
    const topics = [
        { url: "https://baomoi.com/suc-khoe-y-te.epi", tag: "suckhoe" },
        { url: "https://baomoi.com/the-thao.epi", tag: "tapluyen" },
        { url: "https://baomoi.com/dinh-duong-lam-dep.epi", tag: "dinhduong" }
    ];
    let count = 0;
    for (const topic of topics) {
        try {
            const res = await axios.get(topic.url, { headers, timeout: 15000 });
            const $ = cheerio.load(res.data);
            const rawBaiViet = [];
            $("a").each((i, el) => {
                const title = $(el).attr("title") || $(el).text().trim();
                let url = $(el).attr("href");
                if (!title || !url || title.length < 25 || !url.includes(".epi")) return;
                if (!url.startsWith("http")) url = "https://baomoi.com" + url;
                const lower = title.toLowerCase();
                if (tuKhoaLoaiBo.some(keywordDoc => lower.includes(keywordDoc.toLowerCase()))) return;
                rawBaiViet.push({ title, url });
            });
            const unique = Array.from(new Map(rawBaiViet.map(a => [a.url, a])).values());
            for (const baiViet of unique.slice(0, 8)) {
                try {
                    const cleanUrl = baiViet.url.split('?')[0];
                    const docId = Buffer.from(cleanUrl).toString('base64').substring(0, 40).replace(/[^a-zA-Z0-9]/g, '');
                    if ((await db.collection("BaiViet").doc(docId).get()).exists) continue;
                    const detailRes = await axios.get(baiViet.url, { headers, timeout: 15000 });
                    const data = tachNoiDungBaiViet(detailRes.data);
                    const $bm = cheerio.load(detailRes.data);

                    let sourceName = data.originalUrl ? getSourceName(data.originalUrl) : "";
                    const ogSiteName = $bm('meta[property="og:site_name"]').attr('content');
                    if (!sourceName || sourceName === "Báo Mới") {
                        if (ogSiteName && !ogSiteName.toLowerCase().includes("baomoi")) {
                            sourceName = ogSiteName;
                        }
                    }

                    if (!sourceName) sourceName = "Báo Mới";

                    let linkLogo = data.linkLogo;
                    if (!linkLogo) {
                        const fallbackLogo = detailRes.data.match(/https:\/\/photo-baomoi\.bmcdn\.me\/[a-zA-Z0-9_\/]+\.png/i);
                        if (fallbackLogo) linkLogo = fallbackLogo[0];
                    }

                    const hinhAnhDaiDien = $bm('meta[property="og:image"]').attr('content') || "";
                    let noiDungBaiViet = "";
                    let hasNoiDung = false;

                    data.items.forEach(item => {
                        if (item.type === "text") {
                            noiDungBaiViet += item.value + "\n\n";
                            hasNoiDung = true;
                        } else if (item.type === "image" && item.value !== hinhAnhDaiDien && item.value !== linkLogo) {
                            noiDungBaiViet += `[img]${item.value}[/img]\n\n`;
                            hasNoiDung = true;
                        }
                    });

                    if (noiDungBaiViet.length < 100 || !hasNoiDung) {
                        noiDungBaiViet = "Vui lòng truy cập nguồn để xem chi tiết.";
                    }

                    const result = {
                        tenBaiViet: baiViet.title,
                        moTa: baiViet.title,
                        hinhAnhDaiDien: hinhAnhDaiDien,
                        ngayDang: new Date().toISOString(),
                        tag: topic.tag,
                        linkBaiViet: data.originalUrl || cleanUrl,
                        linkLogo: linkLogo,
                        noiDung: noiDungBaiViet
                    };

                    await db.collection("BaiViet").doc(docId).set(result);
                    count++;
                    console.log(`Đã lưu: ${result.tenBaiViet} | Đoạn text: ${data.items.filter(i => i.type === 'text').length}`);
                    await new Promise(r => setTimeout(r, 1000));
                } catch (e) { console.error(`Lỗi bài: ${e.message}`); }
            }
        } catch (e) { console.error(`Lỗi topic: ${e.message}`); }
    }
    return { count };
}
exports.triggerDongBoBaiViet = onRequest({ timeoutSeconds: 300, memory: "512MiB", cors: true }, async (req, res) => {
    try { const r = await dongBoBaiViet(); res.status(200).json(r); } catch (e) { res.status(500).send(e.message); }
});
exports.xoaAllBaiViet = onRequest(async (req, res) => {
    const snap = await admin.firestore().collection("BaiViet").get();
    const batch = admin.firestore().batch();
    snap.docs.forEach(doc => batch.delete(doc.ref));
    await batch.commit();
    res.send("Đã xóa tất cả các bài viết!");
});
exports.dongBoBaiTapLuyen = onSchedule("every 30 minutes", async () => { await dongBoBaiViet(); });