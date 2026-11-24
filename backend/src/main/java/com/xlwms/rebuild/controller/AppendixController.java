package com.xlwms.rebuild.controller;

import com.xlwms.rebuild.common.BizException;
import com.xlwms.rebuild.common.R;
import com.xlwms.rebuild.service.AppendixStore;
import com.xlwms.rebuild.service.AppendixStore.FileMeta;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * PDA 附件 4 个接口 /gateway/wms/pda/appendix/{appendixTypeList,upload,uploadOther,remove} 与图片上传 /gateway/common/pic/upload。
 * 路径、表单字段（files、bizNo、bizCode、customerCode、whCode、bizAppendixType）和响应字段（Appendix、FileTypeBean、
 * CreateClaimOrderViewModel.Upload.Result）取自 APK；本地只记元数据，见 {@link AppendixStore}。
 */
@RestController
public class AppendixController {

    private final AppendixStore store;

    public AppendixController(AppendixStore store) {
        this.store = store;
    }

    private static List<String[]> describe(List<MultipartFile> files) {
        List<String[]> out = new ArrayList<>();
        if (files != null) {
            files.forEach(f -> out.add(new String[] {f.getOriginalFilename(), String.valueOf(f.getSize())}));
        }
        return out;
    }

    /** Appendix */
    private static Map<String, Object> appendix(FileMeta f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("bizAppendixType", f.bizAppendixType);
        m.put("bizAppendixTypeName", AppendixStore.TYPES.getOrDefault(f.bizCode, Map.of()).getOrDefault(f.bizAppendixType, ""));
        m.put("bizCode", f.bizCode);
        m.put("bizName", "");
        m.put("bizNo", f.bizNo);
        m.put("customerCode", f.customerCode);
        m.put("fileExt", f.fileExt);
        m.put("fileKey", f.fileKey);
        m.put("fileName", f.fileName());
        m.put("filePath", "appendix/" + f.fileName());
        m.put("fileSize", String.valueOf(f.fileSize));
        m.put("originName", f.originName);
        m.put("remark", "");
        m.put("tenantCode", "T-DEMO");
        m.put("uploadTime", f.uploadTime);
        m.put("url", f.url());
        m.put("whCode", f.whCode);
        return m;
    }

    /** CreateClaimOrderViewModel.Upload.Result */
    private static Map<String, Object> result(FileMeta f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fileExt", f.fileExt);
        m.put("fileKey", f.fileKey);
        m.put("fileName", f.fileName());
        m.put("fileSize", f.fileSize);
        m.put("fullKeyName", "appendix/" + f.fileName());
        m.put("key", f.fileKey);
        m.put("originName", f.originName);
        m.put("url", f.url());
        return m;
    }

    private FileMeta single(List<MultipartFile> files, Set<String> ext) {
        List<String[]> d = describe(files);
        if (d.size() != 1) {
            throw new BizException(d.isEmpty() ? "请选择文件" : "一次只能上传一个文件");
        }
        return store.add(d.get(0)[0], Long.parseLong(d.get(0)[1]), ext);
    }

    @GetMapping("/gateway/wms/pda/appendix/appendixTypeList")
    public R<List<Map<String, Object>>> typeList(@RequestParam String bizCode, @RequestParam(required = false) String whCode) {
        List<Map<String, Object>> list = new ArrayList<>();
        AppendixStore.types(bizCode).forEach((type, name) ->
                list.add(Map.of("bizAppendixType", type, "bizAppendixTypeName", name, "bizCode", bizCode)));
        return R.ok(list);
    }

    @PostMapping("/gateway/wms/pda/appendix/upload")
    public R<List<Map<String, Object>>> upload(@RequestParam(required = false) String bizNo,
                                               @RequestParam(required = false) String bizCode,
                                               @RequestParam(required = false) String customerCode,
                                               @RequestParam(required = false) String whCode,
                                               @RequestParam(required = false) Integer bizAppendixType,
                                               @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        if (bizNo == null || bizNo.isBlank()) {
            throw new BizException("缺少业务单号 bizNo");
        }
        int type = bizAppendixType == null ? -1 : bizAppendixType;
        if (!AppendixStore.types(bizCode).containsKey(type)) {
            throw new BizException("附件类型不属于该业务：" + bizAppendixType);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (FileMeta f : store.addAll(describe(files), AppendixStore.APPENDIX_EXT)) {
            f.bizNo = bizNo.trim();
            f.bizCode = bizCode.trim();
            f.customerCode = customerCode == null ? "" : customerCode;
            f.whCode = whCode == null ? "" : whCode;
            f.bizAppendixType = type;
            out.add(appendix(f));
        }
        return R.ok(out);
    }

    /** 上传日志文件（APK 里由设置页“上传日志”调用）。 */
    @PostMapping("/gateway/wms/pda/appendix/uploadOther")
    public R<Map<String, Object>> uploadOther(@RequestPart(value = "file", required = false) List<MultipartFile> file,
                                              @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return R.ok(result(single(file != null ? file : files, AppendixStore.LOG_EXT)));
    }

    @GetMapping("/gateway/wms/pda/appendix/remove")
    public R<Object> remove(@RequestParam String fileKey,
                            @RequestParam(required = false) String expressNo,
                            @RequestParam(required = false) String logisticsCarrier,
                            @RequestParam(required = false) String customerCode,
                            @RequestParam(required = false) String customerName,
                            @RequestParam(required = false) String fritTaskNo,
                            @RequestParam(required = false) String whCode) {
        store.remove(fileKey);
        return R.ok(null);
    }

    @PostMapping("/gateway/common/pic/upload")
    public R<Map<String, Object>> uploadPic(@RequestPart(value = "files", required = false) List<MultipartFile> files,
                                            @RequestPart(value = "file", required = false) List<MultipartFile> file) {
        return R.ok(result(single(files != null ? files : file, AppendixStore.IMAGE_EXT)));
    }
}
