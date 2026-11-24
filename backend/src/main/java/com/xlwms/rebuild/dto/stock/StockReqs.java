package com.xlwms.rebuild.dto.stock;

/**
 * PDA 库内作业请求体，字段名取自 APK 中 data.model.bean 的同名 *Req 类（见 docs/PDA_REQUEST_BODIES.md）。
 * 数量字段在 APK 里有的是 String（adjustQty / moveQty / beforeStkQty），这里保持一致。
 */
public final class StockReqs {
    private StockReqs() {
    }

    /** StockCellListReq：/pda/stock/cellList、/pda/stock/customer/cellList */
    public record StockCellListReq(String cellNo, int current, String customerCode, int size, Integer type, String whCode) {
    }

    /** StockDetailReq：/pda/stock/detail */
    public record StockDetailReq(String boxType, int current, String customerCode, String customerName, String customizeBarcode,
                                 String fnsku, Integer productQuality, String productSku, int size, Integer type, String whCode) {
    }

    /** StockSearchReq：/pda/stock/search。queryType 取值是本项目假设：1 SKU/条码，2 FNSKU，3 箱型，4 库位，空 = 全部。 */
    public record StockSearchReq(int current, String queryParam, Integer queryType, Integer searchCell, int size, String whCode) {
    }

    /** StockCheckReq：/pda/stock/check */
    public record StockCheckReq(Integer adjustType, String boxType, String cellNo, String customerCode, String fnsku,
                                Integer productQuality, String productSku, String whCode) {
    }

    /** StockAdjustReq：/pda/stock/adjust。adjustFlag 取值是本项目假设：1 调增 / 2 调减。 */
    public record StockAdjustReq(Integer adjustFlag, String adjustQty, Integer adjustType, String beforeStkQty, String boxType,
                                 String cellNo, String customerCode, String fnsku, String productName, Integer productQuality,
                                 String productSku, String remark, String whCode) {
    }

    /** StockMoveReq：/pda/stock/move */
    public record StockMoveReq(String boxType, String customerCode, String customizeBarcode, String fnsku, String moveQty,
                               Integer moveType, String productName, Integer productQuality, String productSku, String remark,
                               String sourceCellNo, String targetCellNo, String whCode) {
    }

    /** StockCellQualityReq：/pda/stock/stockCellQuality（本项目按“整库位移库”实现，真实语义未确认）。 */
    public record StockCellQualityReq(String sourceCellNo, String targetCellNo, String whCode) {
    }
}
