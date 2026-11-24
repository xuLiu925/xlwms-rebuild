package com.xlwms.rebuild.service;

import com.xlwms.rebuild.common.BizException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 附件与图片上传的本地假实现。只在内存里记录文件元数据（名称、大小、业务单号），<b>不保存文件内容、不写磁盘</b>，
 * 返回的 url 只是占位串，不能下载。业务代码 bizCode 与附件类型编码都是本项目占位：
 * RETURN 退件 / OUTBOUND 出库 / INBOUND 入库 / MESSAGE_BOARD 留言板。
 */
@Component
public class AppendixStore {

    public static final long MAX_SIZE = 10L * 1024 * 1024;
    public static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    public static final Set<String> APPENDIX_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "pdf");
    public static final Set<String> LOG_EXT = Set.of("zip", "log", "txt");

    /** bizCode → (bizAppendixType → 名称) */
    public static final Map<String, Map<Integer, String>> TYPES = new LinkedHashMap<>();

    static {
        TYPES.put("RETURN", ordered(1, "外包装照片", 2, "商品照片", 3, "面单照片"));
        TYPES.put("OUTBOUND", ordered(1, "包裹照片", 4, "其他"));
        TYPES.put("INBOUND", ordered(1, "到货照片", 2, "异常照片", 4, "其他"));
        TYPES.put("MESSAGE_BOARD", ordered(4, "其他"));
    }

    private static Map<Integer, String> ordered(Object... kv) {
        Map<Integer, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((Integer) kv[i], (String) kv[i + 1]);
        }
        return m;
    }

    public static class FileMeta {
        public final String fileKey;
        public final String originName;
        public final String fileExt;
        public final long fileSize;
        public final String uploadTime;
        public String bizCode = "";
        public String bizNo = "";
        public String customerCode = "";
        public String whCode = "";
        public int bizAppendixType;

        FileMeta(String fileKey, String originName, String fileExt, long fileSize, String uploadTime) {
            this.fileKey = fileKey;
            this.originName = originName;
            this.fileExt = fileExt;
            this.fileSize = fileSize;
            this.uploadTime = uploadTime;
        }

        public String fileName() {
            return fileKey + "." + fileExt;
        }

        public String url() {
            return "local-demo://appendix/" + fileName();
        }
    }

    private final Map<String, FileMeta> files = new LinkedHashMap<>();

    public synchronized void reset() {
        files.clear();
    }

    public static Map<Integer, String> types(String bizCode) {
        Map<Integer, String> t = TYPES.get(bizCode == null ? "" : bizCode.trim());
        if (t == null) {
            throw new BizException("不支持的业务代码：" + bizCode);
        }
        return t;
    }

    private static String ext(String name) {
        int i = name == null ? -1 : name.lastIndexOf('.');
        return i < 0 ? "" : name.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    /** 校验并登记一个文件（只记元数据）。 */
    public synchronized FileMeta add(String originName, long size, Set<String> allowedExt) {
        String name = originName == null || originName.isBlank() ? "file" : originName.trim();
        String ext = ext(name);
        if (!allowedExt.contains(ext)) {
            throw new BizException("不支持的文件类型：" + (ext.isEmpty() ? name : ext));
        }
        if (size <= 0) {
            throw new BizException("文件为空：" + name);
        }
        if (size > MAX_SIZE) {
            throw new BizException("文件超过 10MB：" + name);
        }
        String key = UUID.randomUUID().toString().replace("-", "");
        FileMeta m = new FileMeta(key, name, ext, size, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        files.put(key, m);
        return m;
    }

    /** 先校验整批（类型、大小），再全部登记，避免只登记一半。 */
    public synchronized List<FileMeta> addAll(List<String[]> nameAndSize, Set<String> allowedExt) {
        if (nameAndSize.isEmpty()) {
            throw new BizException("请选择文件");
        }
        if (nameAndSize.size() > 9) {
            throw new BizException("一次最多上传 9 个文件");
        }
        for (String[] f : nameAndSize) {
            long size = Long.parseLong(f[1]);
            if (!allowedExt.contains(ext(f[0])) || size <= 0 || size > MAX_SIZE) {
                add(f[0], size, allowedExt);
            }
        }
        List<FileMeta> out = new ArrayList<>();
        for (String[] f : nameAndSize) {
            out.add(add(f[0], Long.parseLong(f[1]), allowedExt));
        }
        return out;
    }

    public synchronized FileMeta get(String fileKey) {
        return files.get(fileKey);
    }

    public synchronized List<FileMeta> byBizNo(String bizNo) {
        return files.values().stream().filter(f -> f.bizNo.equals(bizNo)).toList();
    }

    public synchronized void remove(String fileKey) {
        if (fileKey == null || files.remove(fileKey.trim()) == null) {
            throw new BizException("附件不存在：" + fileKey);
        }
    }
}
