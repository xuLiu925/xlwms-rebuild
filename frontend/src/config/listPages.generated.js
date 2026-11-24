// 由 tools/list-pages/gen.mjs 生成，勿手改。接口路径、字段与编码均为本项目占位；mock 为虚构数据。
export default [
  {
    "key": "customer",
    "route": "customer/list",
    "title": "客户列表",
    "columns": [
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "contactName",
        "label": "联系人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "settleCurrency",
        "label": "结算币种",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "balance",
        "label": "账户余额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "creditLimit",
        "label": "信用额度",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "settleCurrency": [
        "CNY",
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "3100000000000001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "contactName": "示例联系人1",
        "settleCurrency": "USD",
        "balance": 300,
        "creditLimit": 0,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3100000000000002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "contactName": "示例联系人2",
        "settleCurrency": "CNY",
        "balance": 1534.5,
        "creditLimit": 5000,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3100000000000003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "contactName": "示例联系人3",
        "settleCurrency": "EUR",
        "balance": 2769,
        "creditLimit": 10000,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3100000000000004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "contactName": "示例联系人4",
        "settleCurrency": "USD",
        "balance": 4003.5,
        "creditLimit": 15000,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3100000000000005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "contactName": "示例联系人5",
        "settleCurrency": "CNY",
        "balance": 5238,
        "creditLimit": 0,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "customerRecharge",
    "route": "customer/recharge",
    "title": "充值审核",
    "columns": [
      {
        "name": "rechargeNo",
        "label": "充值单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "amount",
        "label": "金额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "payMethod",
        "label": "支付方式",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "10": [
            "待审核",
            "warning"
          ],
          "20": [
            "已通过",
            "success"
          ],
          "30": [
            "已驳回",
            "danger"
          ]
        }
      },
      {
        "name": "applyTime",
        "label": "申请时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "auditTime",
        "label": "审核时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "currency": [
        "CNY",
        "USD"
      ],
      "payMethod": [
        "线上支付",
        "银行转账"
      ]
    },
    "mock": [
      {
        "id": "3200000000000001",
        "rechargeNo": "RC20261100001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "amount": 500,
        "currency": "USD",
        "payMethod": "银行转账",
        "status": 10,
        "statusName": "待审核",
        "applyTime": "2026-11-01 09:00:00",
        "auditTime": ""
      },
      {
        "id": "3200000000000002",
        "rechargeNo": "RC20261100002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "amount": 1000,
        "currency": "CNY",
        "payMethod": "线上支付",
        "status": 20,
        "statusName": "已通过",
        "applyTime": "2026-11-02 10:07:00",
        "auditTime": "2026-11-03 10:07:00"
      },
      {
        "id": "3200000000000003",
        "rechargeNo": "RC20261100003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "amount": 1500,
        "currency": "USD",
        "payMethod": "银行转账",
        "status": 20,
        "statusName": "已通过",
        "applyTime": "2026-11-03 11:14:00",
        "auditTime": "2026-11-04 11:14:00"
      },
      {
        "id": "3200000000000004",
        "rechargeNo": "RC20261100004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "amount": 2000,
        "currency": "CNY",
        "payMethod": "线上支付",
        "status": 30,
        "statusName": "已驳回",
        "applyTime": "2026-11-04 12:21:00",
        "auditTime": "2026-11-05 12:21:00"
      },
      {
        "id": "3200000000000005",
        "rechargeNo": "RC20261100005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "amount": 2500,
        "currency": "USD",
        "payMethod": "银行转账",
        "status": 10,
        "statusName": "待审核",
        "applyTime": "2026-11-05 13:28:00",
        "auditTime": ""
      }
    ],
    "mockTotal": 16
  },
  {
    "key": "seedWall",
    "route": "basicData/seedWall",
    "title": "播种墙",
    "columns": [
      {
        "name": "wallCode",
        "label": "播种墙编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "wallName",
        "label": "名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "rowCount",
        "label": "行数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "colCount",
        "label": "列数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "gridCount",
        "label": "格口数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "3300000000000001",
        "wallCode": "SW001",
        "wallName": "示例播种墙1",
        "whCode": "WH01",
        "rowCount": 3,
        "colCount": 4,
        "gridCount": 12,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3300000000000002",
        "wallCode": "SW002",
        "wallName": "示例播种墙2",
        "whCode": "WH02",
        "rowCount": 4,
        "colCount": 5,
        "gridCount": 20,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3300000000000003",
        "wallCode": "SW003",
        "wallName": "示例播种墙3",
        "whCode": "WH01",
        "rowCount": 5,
        "colCount": 6,
        "gridCount": 30,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3300000000000004",
        "wallCode": "SW004",
        "wallName": "示例播种墙4",
        "whCode": "WH02",
        "rowCount": 3,
        "colCount": 7,
        "gridCount": 21,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3300000000000005",
        "wallCode": "SW005",
        "wallName": "示例播种墙5",
        "whCode": "WH01",
        "rowCount": 4,
        "colCount": 4,
        "gridCount": 16,
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "pickCart",
    "route": "basicData/pickCart",
    "title": "拣货车",
    "columns": [
      {
        "name": "cartCode",
        "label": "拣货车编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "cartName",
        "label": "名称",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "gridCount",
        "label": "格口数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "bindWaveNo",
        "label": "绑定波次",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "空闲",
            "success"
          ],
          "1": [
            "使用中",
            "warning"
          ],
          "2": [
            "停用",
            "info"
          ]
        }
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "3400000000000001",
        "cartCode": "CART001",
        "cartName": "示例拣货车1",
        "whCode": "WH01",
        "gridCount": 12,
        "bindWaveNo": "",
        "status": 0,
        "statusName": "空闲",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3400000000000002",
        "cartCode": "CART002",
        "cartName": "示例拣货车2",
        "whCode": "WH02",
        "gridCount": 16,
        "bindWaveNo": "WV20261100002",
        "status": 1,
        "statusName": "使用中",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3400000000000003",
        "cartCode": "CART003",
        "cartName": "示例拣货车3",
        "whCode": "WH01",
        "gridCount": 24,
        "bindWaveNo": "",
        "status": 0,
        "statusName": "空闲",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3400000000000004",
        "cartCode": "CART004",
        "cartName": "示例拣货车4",
        "whCode": "WH02",
        "gridCount": 12,
        "bindWaveNo": "WV20261100004",
        "status": 1,
        "statusName": "使用中",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3400000000000005",
        "cartCode": "CART005",
        "cartName": "示例拣货车5",
        "whCode": "WH01",
        "gridCount": 16,
        "bindWaveNo": "",
        "status": 2,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "account",
    "route": "auth/account",
    "title": "账号管理",
    "columns": [
      {
        "name": "username",
        "label": "登录账号",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "realName",
        "label": "姓名",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "roleName",
        "label": "角色",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "whCodes",
        "label": "可用仓库",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "lastLoginTime",
        "label": "最近登录",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "roleName": [
        "仓库管理员",
        "复核员",
        "拣货员",
        "收货员"
      ]
    },
    "mock": [
      {
        "id": "3500000000000001",
        "username": "user01",
        "realName": "示例员工1",
        "roleName": "仓库管理员",
        "whCodes": "WH01",
        "status": 1,
        "statusName": "启用",
        "lastLoginTime": "2026-11-03 09:00:00"
      },
      {
        "id": "3500000000000002",
        "username": "user02",
        "realName": "示例员工2",
        "roleName": "收货员",
        "whCodes": "WH02",
        "status": 1,
        "statusName": "启用",
        "lastLoginTime": "2026-11-04 10:07:00"
      },
      {
        "id": "3500000000000003",
        "username": "user03",
        "realName": "示例员工3",
        "roleName": "拣货员",
        "whCodes": "WH01,WH02",
        "status": 1,
        "statusName": "启用",
        "lastLoginTime": "2026-11-05 11:14:00"
      },
      {
        "id": "3500000000000004",
        "username": "user04",
        "realName": "示例员工4",
        "roleName": "复核员",
        "whCodes": "WH01",
        "status": 1,
        "statusName": "启用",
        "lastLoginTime": "2026-11-06 12:21:00"
      },
      {
        "id": "3500000000000005",
        "username": "user05",
        "realName": "示例员工5",
        "roleName": "仓库管理员",
        "whCodes": "WH02",
        "status": 0,
        "statusName": "停用",
        "lastLoginTime": "2026-11-07 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "role",
    "route": "auth/role",
    "title": "角色管理",
    "columns": [
      {
        "name": "roleCode",
        "label": "角色编码",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "roleName",
        "label": "角色名称",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "userCount",
        "label": "账号数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "remark",
        "label": "备注",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {},
    "mock": [
      {
        "id": "3600000000000001",
        "roleCode": "ROLE_ADMIN",
        "roleName": "仓库管理员",
        "userCount": 2,
        "remark": "示例角色 1",
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3600000000000002",
        "roleCode": "ROLE_RECEIVE",
        "roleName": "收货员",
        "userCount": 3,
        "remark": "示例角色 2",
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3600000000000003",
        "roleCode": "ROLE_PICK",
        "roleName": "拣货员",
        "userCount": 4,
        "remark": "示例角色 3",
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3600000000000004",
        "roleCode": "ROLE_REVIEW",
        "roleName": "复核员",
        "userCount": 3,
        "remark": "示例角色 4",
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3600000000000005",
        "roleCode": "ROLE_FINANCE",
        "roleName": "财务",
        "userCount": 1,
        "remark": "示例角色 5",
        "status": 1,
        "statusName": "启用",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 6
  },
  {
    "key": "channel",
    "route": "logisticsManage/channel",
    "title": "物流渠道",
    "columns": [
      {
        "name": "channelCode",
        "label": "渠道代码",
        "type": "str",
        "width": 130,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "channelName",
        "label": "渠道名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "serviceType",
        "label": "服务类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "carrier": [
        "示例承运商A",
        "示例承运商B",
        "示例承运商C"
      ],
      "serviceType": [
        "卡派",
        "快递",
        "自提"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "3700000000000001",
        "channelCode": "CH-001",
        "channelName": "示例渠道1",
        "carrier": "示例承运商A",
        "serviceType": "快递",
        "whCode": "WH01",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3700000000000002",
        "channelCode": "CH-002",
        "channelName": "示例渠道2",
        "carrier": "示例承运商B",
        "serviceType": "卡派",
        "whCode": "WH02",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3700000000000003",
        "channelCode": "CH-003",
        "channelName": "示例渠道3",
        "carrier": "示例承运商C",
        "serviceType": "自提",
        "whCode": "WH01",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3700000000000004",
        "channelCode": "CH-004",
        "channelName": "示例渠道4",
        "carrier": "示例承运商A",
        "serviceType": "快递",
        "whCode": "WH02",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3700000000000005",
        "channelCode": "CH-005",
        "channelName": "示例渠道5",
        "carrier": "示例承运商B",
        "serviceType": "卡派",
        "whCode": "WH01",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "channelGroup",
    "route": "logisticsManage/channelGroup",
    "title": "渠道分组",
    "columns": [
      {
        "name": "groupCode",
        "label": "分组代码",
        "type": "str",
        "width": 130,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "groupName",
        "label": "分组名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "channelCount",
        "label": "渠道数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "remark",
        "label": "备注",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "停用",
            "info"
          ],
          "1": [
            "启用",
            "success"
          ]
        }
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {},
    "mock": [
      {
        "id": "3800000000000001",
        "groupCode": "GRP-001",
        "groupName": "示例分组1",
        "channelCount": 2,
        "remark": "示例渠道分组",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "3800000000000002",
        "groupCode": "GRP-002",
        "groupName": "示例分组2",
        "channelCount": 3,
        "remark": "示例渠道分组",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "3800000000000003",
        "groupCode": "GRP-003",
        "groupName": "示例分组3",
        "channelCount": 4,
        "remark": "示例渠道分组",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "3800000000000004",
        "groupCode": "GRP-004",
        "groupName": "示例分组4",
        "channelCount": 5,
        "remark": "示例渠道分组",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "3800000000000005",
        "groupCode": "GRP-005",
        "groupName": "示例分组5",
        "channelCount": 6,
        "remark": "示例渠道分组",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 5
  },
  {
    "key": "exchangeRate",
    "route": "setting/exchangeRate",
    "title": "汇率",
    "columns": [
      {
        "name": "fromCurrency",
        "label": "原币种",
        "type": "str",
        "width": 90,
        "kw": true,
        "filter": true,
        "enum": null
      },
      {
        "name": "toCurrency",
        "label": "目标币种",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "rate",
        "label": "汇率",
        "type": "rate",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "effectiveDate",
        "label": "生效日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "updateUser",
        "label": "维护人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": null,
    "filterOptions": {
      "fromCurrency": [
        "EUR",
        "GBP",
        "JPY",
        "USD"
      ],
      "toCurrency": [
        "CNY",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "3900000000000001",
        "fromCurrency": "USD",
        "toCurrency": "CNY",
        "rate": 7.1,
        "effectiveDate": "2026-11-01",
        "updateUser": "示例员工1"
      },
      {
        "id": "3900000000000002",
        "fromCurrency": "EUR",
        "toCurrency": "CNY",
        "rate": 7.701,
        "effectiveDate": "2026-11-02",
        "updateUser": "示例员工1"
      },
      {
        "id": "3900000000000003",
        "fromCurrency": "GBP",
        "toCurrency": "CNY",
        "rate": 9.002,
        "effectiveDate": "2026-11-03",
        "updateUser": "示例员工1"
      },
      {
        "id": "3900000000000004",
        "fromCurrency": "JPY",
        "toCurrency": "CNY",
        "rate": 0.048,
        "effectiveDate": "2026-11-04",
        "updateUser": "示例员工1"
      },
      {
        "id": "3900000000000005",
        "fromCurrency": "USD",
        "toCurrency": "USD",
        "rate": 1.001,
        "effectiveDate": "2026-11-05",
        "updateUser": "示例员工1"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "notice",
    "route": "setting/notice",
    "title": "公告",
    "columns": [
      {
        "name": "title",
        "label": "标题",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "noticeType",
        "label": "类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "publisher",
        "label": "发布人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "草稿",
            "info"
          ],
          "1": [
            "已发布",
            "success"
          ],
          "2": [
            "已撤回",
            "warning"
          ]
        }
      },
      {
        "name": "publishTime",
        "label": "发布时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "noticeType": [
        "仓库通知",
        "系统公告"
      ]
    },
    "mock": [
      {
        "id": "4000000000000001",
        "title": "示例公告1",
        "noticeType": "系统公告",
        "publisher": "示例员工1",
        "status": 1,
        "statusName": "已发布",
        "publishTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4000000000000002",
        "title": "示例公告2",
        "noticeType": "仓库通知",
        "publisher": "示例员工2",
        "status": 1,
        "statusName": "已发布",
        "publishTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4000000000000003",
        "title": "示例公告3",
        "noticeType": "系统公告",
        "publisher": "示例员工3",
        "status": 0,
        "statusName": "草稿",
        "publishTime": ""
      },
      {
        "id": "4000000000000004",
        "title": "示例公告4",
        "noticeType": "仓库通知",
        "publisher": "示例员工1",
        "status": 2,
        "statusName": "已撤回",
        "publishTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4000000000000005",
        "title": "示例公告5",
        "noticeType": "系统公告",
        "publisher": "示例员工2",
        "status": 1,
        "statusName": "已发布",
        "publishTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 9
  },
  {
    "key": "requestLog",
    "route": "toolKit/requestLog",
    "title": "请求日志",
    "columns": [
      {
        "name": "requestNo",
        "label": "请求编号",
        "type": "str",
        "width": 160,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "apiName",
        "label": "接口",
        "type": "str",
        "kw": true,
        "filter": true,
        "enum": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "costMs",
        "label": "耗时(ms)",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "result",
        "label": "结果",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "失败",
            "danger"
          ],
          "1": [
            "成功",
            "success"
          ]
        }
      },
      {
        "name": "requestTime",
        "label": "请求时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "result",
    "filterOptions": {
      "apiName": [
        "示例-创建入库单",
        "示例-创建出库单",
        "示例-查询库存"
      ],
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ]
    },
    "mock": [
      {
        "id": "4100000000000001",
        "requestNo": "REQ202611000001",
        "apiName": "示例-创建出库单",
        "customerCode": "C001",
        "costMs": 40,
        "result": 1,
        "resultName": "成功",
        "requestTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4100000000000002",
        "requestNo": "REQ202611000002",
        "apiName": "示例-查询库存",
        "customerCode": "C002",
        "costMs": 77,
        "result": 1,
        "resultName": "成功",
        "requestTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4100000000000003",
        "requestNo": "REQ202611000003",
        "apiName": "示例-创建入库单",
        "customerCode": "C003",
        "costMs": 114,
        "result": 1,
        "resultName": "成功",
        "requestTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4100000000000004",
        "requestNo": "REQ202611000004",
        "apiName": "示例-创建出库单",
        "customerCode": "C004",
        "costMs": 151,
        "result": 0,
        "resultName": "失败",
        "requestTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4100000000000005",
        "requestNo": "REQ202611000005",
        "apiName": "示例-查询库存",
        "customerCode": "C005",
        "costMs": 188,
        "result": 1,
        "resultName": "成功",
        "requestTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 20
  },
  {
    "key": "taskCenter",
    "route": "toolKit/taskCenter",
    "title": "任务中心",
    "columns": [
      {
        "name": "taskNo",
        "label": "任务编号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "taskName",
        "label": "任务名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "taskType",
        "label": "类型",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "progress",
        "label": "进度(%)",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "10": [
            "排队中",
            "info"
          ],
          "20": [
            "执行中",
            "warning"
          ],
          "30": [
            "已完成",
            "success"
          ],
          "40": [
            "失败",
            "danger"
          ]
        }
      },
      {
        "name": "createUser",
        "label": "创建人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "finishTime",
        "label": "完成时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "taskType": [
        "导入",
        "导出"
      ]
    },
    "mock": [
      {
        "id": "4200000000000001",
        "taskNo": "TASK20261100001",
        "taskName": "导出入库单",
        "taskType": "导出",
        "progress": 100,
        "status": 30,
        "statusName": "已完成",
        "createUser": "示例员工1",
        "createTime": "2026-11-01 09:00:00",
        "finishTime": "2026-11-01 10:00:00"
      },
      {
        "id": "4200000000000002",
        "taskNo": "TASK20261100002",
        "taskName": "导出库存",
        "taskType": "导出",
        "progress": 100,
        "status": 30,
        "statusName": "已完成",
        "createUser": "示例员工2",
        "createTime": "2026-11-02 10:07:00",
        "finishTime": "2026-11-02 11:07:00"
      },
      {
        "id": "4200000000000003",
        "taskNo": "TASK20261100003",
        "taskName": "导入产品资料",
        "taskType": "导入",
        "progress": 45,
        "status": 20,
        "statusName": "执行中",
        "createUser": "示例员工3",
        "createTime": "2026-11-03 11:14:00",
        "finishTime": ""
      },
      {
        "id": "4200000000000004",
        "taskNo": "TASK20261100004",
        "taskName": "导出出库单",
        "taskType": "导出",
        "progress": 0,
        "status": 10,
        "statusName": "排队中",
        "createUser": "示例员工1",
        "createTime": "2026-11-04 12:21:00",
        "finishTime": ""
      },
      {
        "id": "4200000000000005",
        "taskNo": "TASK20261100005",
        "taskName": "导出入库单",
        "taskType": "导出",
        "progress": 60,
        "status": 40,
        "statusName": "失败",
        "createUser": "示例员工2",
        "createTime": "2026-11-05 13:28:00",
        "finishTime": "2026-11-05 14:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "returnOrder",
    "route": "returnGoods/warehousing",
    "title": "退货入库",
    "columns": [
      {
        "name": "returnNo",
        "label": "退货单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "sourceOrderNo",
        "label": "原出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "trackingNo",
        "label": "退货跟踪号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "returnType",
        "label": "退货类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "skuQty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "10": [
            "待收货",
            "info"
          ],
          "20": [
            "已收货",
            "warning"
          ],
          "30": [
            "已上架",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        }
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "returnType": [
        "买家退货",
        "异常件",
        "拒收"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4300000000000001",
        "returnNo": "RT20261100001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "sourceOrderNo": "OB20261000011",
        "trackingNo": "TRK00900000",
        "returnType": "买家退货",
        "skuQty": 1,
        "whCode": "WH01",
        "status": 10,
        "statusName": "待收货",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4300000000000002",
        "returnNo": "RT20261100002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "sourceOrderNo": "OB20261000012",
        "trackingNo": "TRK00900013",
        "returnType": "拒收",
        "skuQty": 2,
        "whCode": "WH02",
        "status": 20,
        "statusName": "已收货",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4300000000000003",
        "returnNo": "RT20261100003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "sourceOrderNo": "OB20261000013",
        "trackingNo": "TRK00900026",
        "returnType": "异常件",
        "skuQty": 3,
        "whCode": "WH01",
        "status": 30,
        "statusName": "已上架",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4300000000000004",
        "returnNo": "RT20261100004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "sourceOrderNo": "OB20261000014",
        "trackingNo": "TRK00900039",
        "returnType": "买家退货",
        "skuQty": 4,
        "whCode": "WH02",
        "status": 30,
        "statusName": "已上架",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4300000000000005",
        "returnNo": "RT20261100005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "sourceOrderNo": "OB20261000015",
        "trackingNo": "TRK00900052",
        "returnType": "拒收",
        "skuQty": 1,
        "whCode": "WH01",
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 18
  },
  {
    "key": "outboundAbnormal",
    "route": "outbound/abnormal",
    "title": "异常订单",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "waveNo",
        "label": "波次号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "abnormalType",
        "label": "异常类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "remark",
        "label": "说明",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "待处理",
            "warning"
          ],
          "1": [
            "已处理",
            "success"
          ]
        }
      },
      {
        "name": "reportUser",
        "label": "上报人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "reportTime",
        "label": "上报时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "abnormalType": [
        "库位异常",
        "缺货",
        "面单异常"
      ]
    },
    "mock": [
      {
        "id": "4400000000000001",
        "orderNo": "OB20261100021",
        "waveNo": "WV20261100001",
        "customerCode": "C001",
        "abnormalType": "缺货",
        "remark": "示例异常说明",
        "status": 0,
        "statusName": "待处理",
        "reportUser": "示例员工1",
        "reportTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4400000000000002",
        "orderNo": "OB20261100022",
        "waveNo": "WV20261100002",
        "customerCode": "C002",
        "abnormalType": "库位异常",
        "remark": "示例异常说明",
        "status": 1,
        "statusName": "已处理",
        "reportUser": "示例员工2",
        "reportTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4400000000000003",
        "orderNo": "OB20261100023",
        "waveNo": "WV20261100003",
        "customerCode": "C003",
        "abnormalType": "面单异常",
        "remark": "示例异常说明",
        "status": 1,
        "statusName": "已处理",
        "reportUser": "示例员工3",
        "reportTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4400000000000004",
        "orderNo": "OB20261100024",
        "waveNo": "WV20261100004",
        "customerCode": "C004",
        "abnormalType": "缺货",
        "remark": "示例异常说明",
        "status": 0,
        "statusName": "待处理",
        "reportUser": "示例员工4",
        "reportTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4400000000000005",
        "orderNo": "OB20261100025",
        "waveNo": "WV20261100001",
        "customerCode": "C005",
        "abnormalType": "库位异常",
        "remark": "示例异常说明",
        "status": 1,
        "statusName": "已处理",
        "reportUser": "示例员工1",
        "reportTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 11
  },
  {
    "key": "outboundIntercept",
    "route": "outbound/intercept",
    "title": "拦截订单",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "interceptReason",
        "label": "拦截原因",
        "type": "str",
        "kw": false,
        "filter": true,
        "enum": null
      },
      {
        "name": "orderStatusName",
        "label": "拦截时订单状态",
        "type": "str",
        "width": 130,
        "kw": false,
        "filter": false,
        "enum": null
      },
      {
        "name": "status",
        "label": "状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "10": [
            "拦截中",
            "warning"
          ],
          "20": [
            "拦截成功",
            "success"
          ],
          "30": [
            "拦截失败",
            "danger"
          ]
        }
      },
      {
        "name": "applyTime",
        "label": "申请时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null
      }
    ],
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "interceptReason": [
        "地址错误",
        "客户取消",
        "重复下单"
      ]
    },
    "mock": [
      {
        "id": "4500000000000001",
        "orderNo": "OB20261100041",
        "customerCode": "C001",
        "interceptReason": "客户取消",
        "orderStatusName": "待拣货",
        "status": 10,
        "statusName": "拦截中",
        "applyTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4500000000000002",
        "orderNo": "OB20261100042",
        "customerCode": "C002",
        "interceptReason": "地址错误",
        "orderStatusName": "拣货中",
        "status": 20,
        "statusName": "拦截成功",
        "applyTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4500000000000003",
        "orderNo": "OB20261100043",
        "customerCode": "C003",
        "interceptReason": "重复下单",
        "orderStatusName": "待复核",
        "status": 20,
        "statusName": "拦截成功",
        "applyTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4500000000000004",
        "orderNo": "OB20261100044",
        "customerCode": "C004",
        "interceptReason": "客户取消",
        "orderStatusName": "待拣货",
        "status": 30,
        "statusName": "拦截失败",
        "applyTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4500000000000005",
        "orderNo": "OB20261100045",
        "customerCode": "C005",
        "interceptReason": "地址错误",
        "orderStatusName": "拣货中",
        "status": 10,
        "statusName": "拦截中",
        "applyTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  }
]
