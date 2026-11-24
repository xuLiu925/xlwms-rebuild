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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "contactName",
        "label": "联系人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "settleCurrency",
        "label": "结算币种",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "balance",
        "label": "账户余额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "creditLimit",
        "label": "信用额度",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": true,
    "unique": "customerCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "amount",
        "label": "金额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "payMethod",
        "label": "支付方式",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "applyTime",
        "label": "申请时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      },
      {
        "name": "auditTime",
        "label": "审核时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "wallName",
        "label": "名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "rowCount",
        "label": "行数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "colCount",
        "label": "列数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "gridCount",
        "label": "格口数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": true,
    "unique": "wallCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "cartName",
        "label": "名称",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "gridCount",
        "label": "格口数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "bindWaveNo",
        "label": "绑定波次",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "cartCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "realName",
        "label": "姓名",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "roleName",
        "label": "角色",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCodes",
        "label": "可用仓库",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "lastLoginTime",
        "label": "最近登录",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": true,
    "unique": "username",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "roleName",
        "label": "角色名称",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "userCount",
        "label": "账号数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "remark",
        "label": "备注",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": true,
    "unique": "roleCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "渠道名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "serviceType",
        "label": "服务类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "channelCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "groupName",
        "label": "分组名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelCount",
        "label": "渠道数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "remark",
        "label": "备注",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "groupCode",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "toCurrency",
        "label": "目标币种",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "rate",
        "label": "汇率",
        "type": "rate",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "effectiveDate",
        "label": "生效日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "updateUser",
        "label": "维护人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      }
    ],
    "editable": true,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "noticeType",
        "label": "类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "publisher",
        "label": "发布人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "publishTime",
        "label": "发布时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": true,
    "unique": "title",
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "apiName",
        "label": "接口",
        "type": "str",
        "kw": true,
        "filter": true,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "costMs",
        "label": "耗时(ms)",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "requestTime",
        "label": "请求时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "taskName",
        "label": "任务名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "taskType",
        "label": "类型",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "progress",
        "label": "进度(%)",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createUser",
        "label": "创建人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      },
      {
        "name": "finishTime",
        "label": "完成时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "sourceOrderNo",
        "label": "原出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "trackingNo",
        "label": "退货跟踪号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "returnType",
        "label": "退货类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "waveNo",
        "label": "波次号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "abnormalType",
        "label": "异常类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "remark",
        "label": "说明",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "reportUser",
        "label": "上报人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "reportTime",
        "label": "上报时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
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
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "interceptReason",
        "label": "拦截原因",
        "type": "str",
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "orderStatusName",
        "label": "拦截时订单状态",
        "type": "str",
        "width": 130,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "applyTime",
        "label": "申请时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
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
  },
  {
    "key": "globalAsn",
    "route": "globalOrder/asn",
    "title": "全局入库单",
    "columns": [
      {
        "name": "asnNo",
        "label": "入库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "箱数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "产品数量",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "在途",
            "info"
          ],
          "11": [
            "收货中",
            "warning"
          ],
          "15": [
            "已收货",
            "primary"
          ],
          "30": [
            "已上架",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "etaDate",
        "label": "预计到达",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4600000000000001",
        "asnNo": "ASN20261100001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "whCode": "WH01",
        "boxCount": 2,
        "skuQty": 20,
        "status": 10,
        "statusName": "在途",
        "etaDate": "2026-11-04",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4600000000000002",
        "asnNo": "ASN20261100002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "whCode": "WH02",
        "boxCount": 3,
        "skuQty": 27,
        "status": 11,
        "statusName": "收货中",
        "etaDate": "2026-11-05",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4600000000000003",
        "asnNo": "ASN20261100003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "whCode": "WH01",
        "boxCount": 4,
        "skuQty": 34,
        "status": 15,
        "statusName": "已收货",
        "etaDate": "2026-11-06",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4600000000000004",
        "asnNo": "ASN20261100004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "whCode": "WH02",
        "boxCount": 5,
        "skuQty": 41,
        "status": 30,
        "statusName": "已上架",
        "etaDate": "2026-11-07",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4600000000000005",
        "asnNo": "ASN20261100005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "whCode": "WH01",
        "boxCount": 6,
        "skuQty": 48,
        "status": 30,
        "statusName": "已上架",
        "etaDate": "2026-11-08",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 16
  },
  {
    "key": "globalParcel",
    "route": "globalOrder/parcel",
    "title": "全局一件代发",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "platformOrderNo",
        "label": "平台单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "trackingNo",
        "label": "跟踪号",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "info"
          ],
          "20": [
            "待拣货",
            "info"
          ],
          "30": [
            "拣货中",
            "warning"
          ],
          "40": [
            "待复核",
            "warning"
          ],
          "50": [
            "已出库",
            "success"
          ],
          "80": [
            "已拦截",
            "danger"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ]
    },
    "mock": [
      {
        "id": "4610000000000001",
        "orderNo": "OB20261100001",
        "platformOrderNo": "PO-DEMO-00001",
        "customerCode": "C001",
        "whCode": "WH01",
        "channelName": "示例渠道1",
        "trackingNo": "",
        "skuQty": 1,
        "status": 10,
        "statusName": "待审核",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4610000000000002",
        "orderNo": "OB20261100002",
        "platformOrderNo": "PO-DEMO-00002",
        "customerCode": "C002",
        "whCode": "WH02",
        "channelName": "示例渠道2",
        "trackingNo": "",
        "skuQty": 2,
        "status": 20,
        "statusName": "待拣货",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4610000000000003",
        "orderNo": "OB20261100003",
        "platformOrderNo": "PO-DEMO-00003",
        "customerCode": "C003",
        "whCode": "WH01",
        "channelName": "示例渠道3",
        "trackingNo": "",
        "skuQty": 3,
        "status": 30,
        "statusName": "拣货中",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4610000000000004",
        "orderNo": "OB20261100004",
        "platformOrderNo": "PO-DEMO-00004",
        "customerCode": "C004",
        "whCode": "WH02",
        "channelName": "示例渠道4",
        "trackingNo": "TRK00100051",
        "skuQty": 1,
        "status": 40,
        "statusName": "待复核",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4610000000000005",
        "orderNo": "OB20261100005",
        "platformOrderNo": "PO-DEMO-00005",
        "customerCode": "C005",
        "whCode": "WH01",
        "channelName": "示例渠道1",
        "trackingNo": "TRK00100068",
        "skuQty": 2,
        "status": 50,
        "statusName": "已出库",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 18
  },
  {
    "key": "globalFreight",
    "route": "globalOrder/freight",
    "title": "全局大货出库",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "箱数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "palletCount",
        "label": "托数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "deliveryType",
        "label": "出库方式",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待处理",
            "info"
          ],
          "20": [
            "作业中",
            "warning"
          ],
          "30": [
            "已出库",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "deliveryType": [
        "卡派",
        "快递",
        "自提"
      ]
    },
    "mock": [
      {
        "id": "4620000000000001",
        "orderNo": "FO20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "boxCount": 10,
        "palletCount": 1,
        "deliveryType": "卡派",
        "status": 10,
        "statusName": "待处理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4620000000000002",
        "orderNo": "FO20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "boxCount": 13,
        "palletCount": 2,
        "deliveryType": "自提",
        "status": 20,
        "statusName": "作业中",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4620000000000003",
        "orderNo": "FO20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "boxCount": 16,
        "palletCount": 3,
        "deliveryType": "快递",
        "status": 30,
        "statusName": "已出库",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4620000000000004",
        "orderNo": "FO20261100004",
        "customerCode": "C004",
        "whCode": "WH02",
        "boxCount": 19,
        "palletCount": 4,
        "deliveryType": "卡派",
        "status": 30,
        "statusName": "已出库",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4620000000000005",
        "orderNo": "FO20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "boxCount": 22,
        "palletCount": 1,
        "deliveryType": "自提",
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "globalReturn",
    "route": "globalOrder/returnOrder",
    "title": "全局退货单",
    "columns": [
      {
        "name": "returnNo",
        "label": "退货单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "returnType",
        "label": "退货类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "returnType": [
        "买家退货",
        "异常件",
        "拒收"
      ]
    },
    "mock": [
      {
        "id": "4630000000000001",
        "returnNo": "GR20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "returnType": "买家退货",
        "skuQty": 1,
        "status": 10,
        "statusName": "待收货",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4630000000000002",
        "returnNo": "GR20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "returnType": "拒收",
        "skuQty": 2,
        "status": 20,
        "statusName": "已收货",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4630000000000003",
        "returnNo": "GR20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "returnType": "异常件",
        "skuQty": 3,
        "status": 30,
        "statusName": "已上架",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4630000000000004",
        "returnNo": "GR20261100004",
        "customerCode": "C004",
        "whCode": "WH02",
        "returnType": "买家退货",
        "skuQty": 4,
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4630000000000005",
        "returnNo": "GR20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "returnType": "拒收",
        "skuQty": 5,
        "status": 10,
        "statusName": "待收货",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "directMailOrder",
    "route": "globalOrder/directMailOrder",
    "title": "直邮订单",
    "columns": [
      {
        "name": "orderNo",
        "label": "订单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "trackingNo",
        "label": "跟踪号",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "weightKg",
        "label": "重量(kg)",
        "type": "num",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "待打单",
            "info"
          ],
          "20": [
            "已打单",
            "warning"
          ],
          "30": [
            "已交接",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ]
    },
    "mock": [
      {
        "id": "4640000000000001",
        "orderNo": "DM20261100001",
        "customerCode": "C001",
        "channelName": "示例渠道1",
        "trackingNo": "",
        "weightKg": 0.3,
        "status": 10,
        "statusName": "待打单",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4640000000000002",
        "orderNo": "DM20261100002",
        "customerCode": "C002",
        "channelName": "示例渠道2",
        "trackingNo": "DMT00200011",
        "weightKg": 0.57,
        "status": 20,
        "statusName": "已打单",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4640000000000003",
        "orderNo": "DM20261100003",
        "customerCode": "C003",
        "channelName": "示例渠道3",
        "trackingNo": "DMT00200022",
        "weightKg": 0.84,
        "status": 30,
        "statusName": "已交接",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4640000000000004",
        "orderNo": "DM20261100004",
        "customerCode": "C004",
        "channelName": "示例渠道4",
        "trackingNo": "DMT00200033",
        "weightKg": 1.11,
        "status": 30,
        "statusName": "已交接",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4640000000000005",
        "orderNo": "DM20261100005",
        "customerCode": "C005",
        "channelName": "示例渠道1",
        "trackingNo": "",
        "weightKg": 1.38,
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "directMailBox",
    "route": "globalOrder/directMailBox",
    "title": "直邮箱",
    "columns": [
      {
        "name": "boxNo",
        "label": "箱号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "orderCount",
        "label": "订单数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "weightKg",
        "label": "重量(kg)",
        "type": "num",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "装箱中",
            "warning"
          ],
          "20": [
            "已封箱",
            "primary"
          ],
          "30": [
            "已交接",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ]
    },
    "mock": [
      {
        "id": "4650000000000001",
        "boxNo": "DMB20261100001",
        "channelName": "示例渠道1",
        "orderCount": 5,
        "weightKg": 3,
        "status": 10,
        "statusName": "装箱中",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4650000000000002",
        "boxNo": "DMB20261100002",
        "channelName": "示例渠道2",
        "orderCount": 7,
        "weightKg": 4.3,
        "status": 20,
        "statusName": "已封箱",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4650000000000003",
        "boxNo": "DMB20261100003",
        "channelName": "示例渠道3",
        "orderCount": 9,
        "weightKg": 5.6,
        "status": 30,
        "statusName": "已交接",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4650000000000004",
        "boxNo": "DMB20261100004",
        "channelName": "示例渠道4",
        "orderCount": 11,
        "weightKg": 6.9,
        "status": 10,
        "statusName": "装箱中",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4650000000000005",
        "boxNo": "DMB20261100005",
        "channelName": "示例渠道1",
        "orderCount": 13,
        "weightKg": 8.2,
        "status": 20,
        "statusName": "已封箱",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "claim",
    "route": "globalOrder/claim",
    "title": "理赔单",
    "columns": [
      {
        "name": "claimNo",
        "label": "理赔单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "orderNo",
        "label": "关联单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "claimType",
        "label": "理赔类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "claimAmount",
        "label": "理赔金额",
        "type": "money",
        "width": 100,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待处理",
            "info"
          ],
          "20": [
            "处理中",
            "warning"
          ],
          "30": [
            "已赔付",
            "success"
          ],
          "40": [
            "已驳回",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "claimType": [
        "丢件",
        "少件",
        "破损"
      ],
      "currency": [
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4660000000000001",
        "claimNo": "CL20261100001",
        "orderNo": "OB20261100031",
        "customerCode": "C001",
        "claimType": "丢件",
        "claimAmount": 15,
        "currency": "USD",
        "status": 10,
        "statusName": "待处理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4660000000000002",
        "claimNo": "CL20261100002",
        "orderNo": "OB20261100032",
        "customerCode": "C002",
        "claimType": "破损",
        "claimAmount": 27.5,
        "currency": "EUR",
        "status": 20,
        "statusName": "处理中",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4660000000000003",
        "claimNo": "CL20261100003",
        "orderNo": "OB20261100033",
        "customerCode": "C003",
        "claimType": "少件",
        "claimAmount": 40,
        "currency": "USD",
        "status": 30,
        "statusName": "已赔付",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4660000000000004",
        "claimNo": "CL20261100004",
        "orderNo": "OB20261100034",
        "customerCode": "C004",
        "claimType": "丢件",
        "claimAmount": 52.5,
        "currency": "EUR",
        "status": 40,
        "statusName": "已驳回",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4660000000000005",
        "claimNo": "CL20261100005",
        "orderNo": "OB20261100035",
        "customerCode": "C005",
        "claimType": "破损",
        "claimAmount": 65,
        "currency": "USD",
        "status": 10,
        "statusName": "待处理",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "workOrder",
    "route": "globalOrder/workOrder",
    "title": "工单",
    "columns": [
      {
        "name": "workOrderNo",
        "label": "工单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "title",
        "label": "标题",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "workType",
        "label": "工单类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "priority",
        "label": "优先级",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待受理",
            "info"
          ],
          "20": [
            "处理中",
            "warning"
          ],
          "30": [
            "已完成",
            "success"
          ],
          "90": [
            "已关闭",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "workType": [
        "拍照",
        "换标",
        "核查",
        "贴标"
      ],
      "priority": [
        "中",
        "低",
        "高"
      ]
    },
    "mock": [
      {
        "id": "4670000000000001",
        "workOrderNo": "WO20261100001",
        "title": "示例工单-换标1",
        "customerCode": "C001",
        "workType": "换标",
        "priority": "高",
        "status": 10,
        "statusName": "待受理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4670000000000002",
        "workOrderNo": "WO20261100002",
        "title": "示例工单-贴标2",
        "customerCode": "C002",
        "workType": "贴标",
        "priority": "中",
        "status": 20,
        "statusName": "处理中",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4670000000000003",
        "workOrderNo": "WO20261100003",
        "title": "示例工单-拍照3",
        "customerCode": "C003",
        "workType": "拍照",
        "priority": "低",
        "status": 30,
        "statusName": "已完成",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4670000000000004",
        "workOrderNo": "WO20261100004",
        "title": "示例工单-核查4",
        "customerCode": "C004",
        "workType": "核查",
        "priority": "高",
        "status": 30,
        "statusName": "已完成",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4670000000000005",
        "workOrderNo": "WO20261100005",
        "title": "示例工单-换标5",
        "customerCode": "C005",
        "workType": "换标",
        "priority": "中",
        "status": 90,
        "statusName": "已关闭",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "arrivalScan",
    "route": "inbound/arrivalScan",
    "title": "到货登记",
    "columns": [
      {
        "name": "scanNo",
        "label": "登记号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "asnNo",
        "label": "入库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "到货箱数",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "scanUser",
        "label": "登记人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "status",
        "label": "匹配状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "未匹配",
            "warning"
          ],
          "1": [
            "已匹配",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "scanTime",
        "label": "登记时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4680000000000001",
        "scanNo": "AR20261100001",
        "asnNo": "ASN20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "boxCount": 1,
        "scanUser": "示例员工1",
        "status": 1,
        "statusName": "已匹配",
        "scanTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4680000000000002",
        "scanNo": "AR20261100002",
        "asnNo": "ASN20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "boxCount": 2,
        "scanUser": "示例员工2",
        "status": 1,
        "statusName": "已匹配",
        "scanTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4680000000000003",
        "scanNo": "AR20261100003",
        "asnNo": "ASN20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "boxCount": 3,
        "scanUser": "示例员工3",
        "status": 1,
        "statusName": "已匹配",
        "scanTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4680000000000004",
        "scanNo": "AR20261100004",
        "asnNo": "",
        "customerCode": "",
        "whCode": "WH02",
        "boxCount": 4,
        "scanUser": "示例员工1",
        "status": 0,
        "statusName": "未匹配",
        "scanTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4680000000000005",
        "scanNo": "AR20261100005",
        "asnNo": "ASN20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "boxCount": 5,
        "scanUser": "示例员工2",
        "status": 1,
        "statusName": "已匹配",
        "scanTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "deliveryGoods",
    "route": "inbound/deliveryGoods",
    "title": "送货预约",
    "columns": [
      {
        "name": "appointNo",
        "label": "预约号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "asnNo",
        "label": "入库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "appointDate",
        "label": "预约日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "vehicleType",
        "label": "车型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待确认",
            "info"
          ],
          "20": [
            "已确认",
            "warning"
          ],
          "30": [
            "已到仓",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "vehicleType": [
        "厢式货车",
        "小货车",
        "集装箱"
      ]
    },
    "mock": [
      {
        "id": "4690000000000001",
        "appointNo": "AP20261100001",
        "asnNo": "ASN20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "appointDate": "2026-11-05",
        "vehicleType": "小货车",
        "status": 10,
        "statusName": "待确认",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4690000000000002",
        "appointNo": "AP20261100002",
        "asnNo": "ASN20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "appointDate": "2026-11-06",
        "vehicleType": "厢式货车",
        "status": 20,
        "statusName": "已确认",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4690000000000003",
        "appointNo": "AP20261100003",
        "asnNo": "ASN20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "appointDate": "2026-11-07",
        "vehicleType": "集装箱",
        "status": 30,
        "statusName": "已到仓",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4690000000000004",
        "appointNo": "AP20261100004",
        "asnNo": "ASN20261100004",
        "customerCode": "C004",
        "whCode": "WH02",
        "appointDate": "2026-11-08",
        "vehicleType": "小货车",
        "status": 30,
        "statusName": "已到仓",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4690000000000005",
        "appointNo": "AP20261100005",
        "asnNo": "ASN20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "appointDate": "2026-11-09",
        "vehicleType": "厢式货车",
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "palletReceive",
    "route": "inbound/palletReceive",
    "title": "托盘收货记录",
    "columns": [
      {
        "name": "receiptNo",
        "label": "收货单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "asnNo",
        "label": "入库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "trayNo",
        "label": "托盘号",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "箱数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "产品数量",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "operator",
        "label": "操作人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "收货中",
            "warning"
          ],
          "20": [
            "已完成",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "receiptTime",
        "label": "收货时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4700000000000001",
        "receiptNo": "PR20261100001",
        "asnNo": "ASN20261100001",
        "trayNo": "TRAY000001",
        "customerCode": "C001",
        "whCode": "WH01",
        "boxCount": 4,
        "skuQty": 40,
        "operator": "示例员工1",
        "status": 10,
        "statusName": "收货中",
        "receiptTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4700000000000002",
        "receiptNo": "PR20261100002",
        "asnNo": "ASN20261100002",
        "trayNo": "TRAY000002",
        "customerCode": "C002",
        "whCode": "WH02",
        "boxCount": 5,
        "skuQty": 45,
        "operator": "示例员工2",
        "status": 20,
        "statusName": "已完成",
        "receiptTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4700000000000003",
        "receiptNo": "PR20261100003",
        "asnNo": "ASN20261100003",
        "trayNo": "TRAY000003",
        "customerCode": "C003",
        "whCode": "WH01",
        "boxCount": 6,
        "skuQty": 50,
        "operator": "示例员工3",
        "status": 20,
        "statusName": "已完成",
        "receiptTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4700000000000004",
        "receiptNo": "PR20261100004",
        "asnNo": "ASN20261100004",
        "trayNo": "TRAY000004",
        "customerCode": "C004",
        "whCode": "WH02",
        "boxCount": 7,
        "skuQty": 55,
        "operator": "示例员工4",
        "status": 20,
        "statusName": "已完成",
        "receiptTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4700000000000005",
        "receiptNo": "PR20261100005",
        "asnNo": "ASN20261100005",
        "trayNo": "TRAY000005",
        "customerCode": "C005",
        "whCode": "WH01",
        "boxCount": 8,
        "skuQty": 60,
        "operator": "示例员工1",
        "status": 10,
        "statusName": "收货中",
        "receiptTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "palletForming",
    "route": "inbound/palletForming",
    "title": "组托记录",
    "columns": [
      {
        "name": "formNo",
        "label": "组托单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "trayNo",
        "label": "托盘号",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "asnNo",
        "label": "入库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "箱数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "operator",
        "label": "操作人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "组托中",
            "warning"
          ],
          "20": [
            "已完成",
            "primary"
          ],
          "30": [
            "已上架",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4710000000000001",
        "formNo": "PF20261100001",
        "trayNo": "TRAY000021",
        "asnNo": "ASN20261100004",
        "whCode": "WH01",
        "boxCount": 6,
        "operator": "示例员工1",
        "status": 10,
        "statusName": "组托中",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4710000000000002",
        "formNo": "PF20261100002",
        "trayNo": "TRAY000022",
        "asnNo": "ASN20261100005",
        "whCode": "WH02",
        "boxCount": 7,
        "operator": "示例员工2",
        "status": 20,
        "statusName": "已完成",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4710000000000003",
        "formNo": "PF20261100003",
        "trayNo": "TRAY000023",
        "asnNo": "ASN20261100006",
        "whCode": "WH01",
        "boxCount": 8,
        "operator": "示例员工3",
        "status": 30,
        "statusName": "已上架",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4710000000000004",
        "formNo": "PF20261100004",
        "trayNo": "TRAY000024",
        "asnNo": "ASN20261100007",
        "whCode": "WH02",
        "boxCount": 9,
        "operator": "示例员工1",
        "status": 10,
        "statusName": "组托中",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4710000000000005",
        "formNo": "PF20261100005",
        "trayNo": "TRAY000025",
        "asnNo": "ASN20261100008",
        "whCode": "WH01",
        "boxCount": 10,
        "operator": "示例员工2",
        "status": 20,
        "statusName": "已完成",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "newProduct",
    "route": "inbound/newProduct",
    "title": "新品维护",
    "columns": [
      {
        "name": "sku",
        "label": "SKU",
        "type": "str",
        "width": 110,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "productName",
        "label": "产品名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "asnNo",
        "label": "来源入库单",
        "type": "str",
        "width": 150,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "lengthCm",
        "label": "长(cm)",
        "type": "num",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "widthCm",
        "label": "宽(cm)",
        "type": "num",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "heightCm",
        "label": "高(cm)",
        "type": "num",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "weightKg",
        "label": "重(kg)",
        "type": "num",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "待测量",
            "warning"
          ],
          "1": [
            "已测量",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
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
        "id": "4720000000000001",
        "sku": "NEW-0001",
        "productName": "示例新品1",
        "customerCode": "C001",
        "asnNo": "ASN20261100001",
        "lengthCm": 0,
        "widthCm": 0,
        "heightCm": 0,
        "weightKg": 0,
        "status": 0,
        "statusName": "待测量",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4720000000000002",
        "sku": "NEW-0002",
        "productName": "示例新品2",
        "customerCode": "C002",
        "asnNo": "ASN20261100002",
        "lengthCm": 11,
        "widthCm": 8.5,
        "heightCm": 5.3,
        "weightKg": 0.3,
        "status": 1,
        "statusName": "已测量",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4720000000000003",
        "sku": "NEW-0003",
        "productName": "示例新品3",
        "customerCode": "C003",
        "asnNo": "ASN20261100003",
        "lengthCm": 12,
        "widthCm": 9,
        "heightCm": 5.6,
        "weightKg": 0.4,
        "status": 1,
        "statusName": "已测量",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4720000000000004",
        "sku": "NEW-0004",
        "productName": "示例新品4",
        "customerCode": "C004",
        "asnNo": "ASN20261100004",
        "lengthCm": 0,
        "widthCm": 0,
        "heightCm": 0,
        "weightKg": 0,
        "status": 0,
        "statusName": "待测量",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4720000000000005",
        "sku": "NEW-0005",
        "productName": "示例新品5",
        "customerCode": "C005",
        "asnNo": "ASN20261100005",
        "lengthCm": 14,
        "widthCm": 10,
        "heightCm": 6.2,
        "weightKg": 0.6,
        "status": 1,
        "statusName": "已测量",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "inventoryClaim",
    "route": "inbound/inventoryClaim",
    "title": "库存认领",
    "columns": [
      {
        "name": "claimNo",
        "label": "认领单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "barcode",
        "label": "条码",
        "type": "str",
        "width": 130,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "cellCode",
        "label": "暂存库位",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "qty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "claimCustomerCode",
        "label": "认领客户",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待认领",
            "warning"
          ],
          "20": [
            "已认领",
            "success"
          ],
          "30": [
            "已作废",
            "info"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ],
      "claimCustomerCode": [
        "C001",
        "C002",
        "C003",
        "C005"
      ]
    },
    "mock": [
      {
        "id": "4730000000000001",
        "claimNo": "IC20261100001",
        "barcode": "UNK00500000",
        "whCode": "WH01",
        "cellCode": "TMP-01",
        "qty": 1,
        "claimCustomerCode": "",
        "status": 10,
        "statusName": "待认领",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4730000000000002",
        "claimNo": "IC20261100002",
        "barcode": "UNK00500007",
        "whCode": "WH02",
        "cellCode": "TMP-02",
        "qty": 2,
        "claimCustomerCode": "C002",
        "status": 20,
        "statusName": "已认领",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4730000000000003",
        "claimNo": "IC20261100003",
        "barcode": "UNK00500014",
        "whCode": "WH01",
        "cellCode": "TMP-03",
        "qty": 3,
        "claimCustomerCode": "C003",
        "status": 20,
        "statusName": "已认领",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4730000000000004",
        "claimNo": "IC20261100004",
        "barcode": "UNK00500021",
        "whCode": "WH02",
        "cellCode": "TMP-04",
        "qty": 4,
        "claimCustomerCode": "",
        "status": 30,
        "statusName": "已作废",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4730000000000005",
        "claimNo": "IC20261100005",
        "barcode": "UNK00500028",
        "whCode": "WH01",
        "cellCode": "TMP-05",
        "qty": 5,
        "claimCustomerCode": "",
        "status": 10,
        "statusName": "待认领",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "outFreight",
    "route": "outbound/freight",
    "title": "大货出库",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "boxCount",
        "label": "箱数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "palletCount",
        "label": "托数",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "deliveryType",
        "label": "出库方式",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "appointTime",
        "label": "预约提货",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
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
            "待拣货",
            "info"
          ],
          "20": [
            "拣货中",
            "warning"
          ],
          "30": [
            "待装柜",
            "warning"
          ],
          "40": [
            "已出库",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "deliveryType": [
        "卡派",
        "快递",
        "自提"
      ]
    },
    "mock": [
      {
        "id": "4740000000000001",
        "orderNo": "FO20261100021",
        "customerCode": "C001",
        "whCode": "WH01",
        "boxCount": 8,
        "palletCount": 1,
        "deliveryType": "卡派",
        "appointTime": "2026-11-03 14:00:00",
        "status": 10,
        "statusName": "待拣货",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4740000000000002",
        "orderNo": "FO20261100022",
        "customerCode": "C002",
        "whCode": "WH02",
        "boxCount": 12,
        "palletCount": 2,
        "deliveryType": "自提",
        "appointTime": "2026-11-04 15:07:00",
        "status": 20,
        "statusName": "拣货中",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4740000000000003",
        "orderNo": "FO20261100023",
        "customerCode": "C003",
        "whCode": "WH01",
        "boxCount": 16,
        "palletCount": 3,
        "deliveryType": "快递",
        "appointTime": "2026-11-05 16:14:00",
        "status": 30,
        "statusName": "待装柜",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4740000000000004",
        "orderNo": "FO20261100024",
        "customerCode": "C004",
        "whCode": "WH02",
        "boxCount": 20,
        "palletCount": 1,
        "deliveryType": "卡派",
        "appointTime": "2026-11-06 17:21:00",
        "status": 40,
        "statusName": "已出库",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4740000000000005",
        "orderNo": "FO20261100025",
        "customerCode": "C005",
        "whCode": "WH01",
        "boxCount": 24,
        "palletCount": 2,
        "deliveryType": "自提",
        "appointTime": "2026-11-07 18:28:00",
        "status": 40,
        "statusName": "已出库",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "waveRule",
    "route": "outbound/waveRule",
    "title": "波次规则",
    "columns": [
      {
        "name": "ruleCode",
        "label": "规则编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "ruleName",
        "label": "规则名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "orderType",
        "label": "订单类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "maxOrderCount",
        "label": "单波次上限",
        "type": "int",
        "width": 100,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "priority",
        "label": "优先级",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "ruleCode",
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ],
      "orderType": [
        "一票一件",
        "一票多件",
        "混合"
      ]
    },
    "mock": [
      {
        "id": "4750000000000001",
        "ruleCode": "WR-001",
        "ruleName": "示例波次规则1",
        "whCode": "WH01",
        "orderType": "一票一件",
        "maxOrderCount": 50,
        "priority": 1,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4750000000000002",
        "ruleCode": "WR-002",
        "ruleName": "示例波次规则2",
        "whCode": "WH02",
        "orderType": "一票多件",
        "maxOrderCount": 100,
        "priority": 2,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4750000000000003",
        "ruleCode": "WR-003",
        "ruleName": "示例波次规则3",
        "whCode": "WH01",
        "orderType": "混合",
        "maxOrderCount": 200,
        "priority": 3,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4750000000000004",
        "ruleCode": "WR-004",
        "ruleName": "示例波次规则4",
        "whCode": "WH02",
        "orderType": "一票一件",
        "maxOrderCount": 50,
        "priority": 4,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4750000000000005",
        "ruleCode": "WR-005",
        "ruleName": "示例波次规则5",
        "whCode": "WH01",
        "orderType": "一票多件",
        "maxOrderCount": 100,
        "priority": 5,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "scanForm",
    "route": "outbound/scanForm",
    "title": "交接单",
    "columns": [
      {
        "name": "formNo",
        "label": "交接单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "parcelCount",
        "label": "包裹数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "totalWeightKg",
        "label": "总重(kg)",
        "type": "num",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "待交接",
            "warning"
          ],
          "20": [
            "已交接",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "handoverTime",
        "label": "交接时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "carrier": [
        "示例承运商A",
        "示例承运商B",
        "示例承运商C"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4760000000000001",
        "formNo": "SF20261100001",
        "carrier": "示例承运商A",
        "whCode": "WH01",
        "parcelCount": 20,
        "totalWeightKg": 15,
        "status": 10,
        "statusName": "待交接",
        "handoverTime": "",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4760000000000002",
        "formNo": "SF20261100002",
        "carrier": "示例承运商B",
        "whCode": "WH02",
        "parcelCount": 26,
        "totalWeightKg": 19.4,
        "status": 20,
        "statusName": "已交接",
        "handoverTime": "2026-11-02 18:07:00",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4760000000000003",
        "formNo": "SF20261100003",
        "carrier": "示例承运商C",
        "whCode": "WH01",
        "parcelCount": 32,
        "totalWeightKg": 23.8,
        "status": 20,
        "statusName": "已交接",
        "handoverTime": "2026-11-03 19:14:00",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4760000000000004",
        "formNo": "SF20261100004",
        "carrier": "示例承运商A",
        "whCode": "WH02",
        "parcelCount": 38,
        "totalWeightKg": 28.2,
        "status": 10,
        "statusName": "待交接",
        "handoverTime": "",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4760000000000005",
        "formNo": "SF20261100005",
        "carrier": "示例承运商B",
        "whCode": "WH01",
        "parcelCount": 44,
        "totalWeightKg": 32.6,
        "status": 20,
        "statusName": "已交接",
        "handoverTime": "2026-11-05 21:28:00",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "splitOrder",
    "route": "outbound/split",
    "title": "拆单记录",
    "columns": [
      {
        "name": "orderNo",
        "label": "原出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "newOrderNos",
        "label": "拆分后单号",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "splitReason",
        "label": "拆单原因",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "operator",
        "label": "操作人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "splitTime",
        "label": "拆单时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": null,
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "splitReason": [
        "渠道限制",
        "缺货",
        "超重"
      ]
    },
    "mock": [
      {
        "id": "4770000000000001",
        "orderNo": "OB20261100061",
        "newOrderNos": "OB20261100061-1,OB20261100061-2",
        "customerCode": "C001",
        "splitReason": "超重",
        "operator": "示例员工1",
        "splitTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4770000000000002",
        "orderNo": "OB20261100062",
        "newOrderNos": "OB20261100062-1,OB20261100062-2",
        "customerCode": "C002",
        "splitReason": "缺货",
        "operator": "示例员工2",
        "splitTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4770000000000003",
        "orderNo": "OB20261100063",
        "newOrderNos": "OB20261100063-1,OB20261100063-2",
        "customerCode": "C003",
        "splitReason": "渠道限制",
        "operator": "示例员工3",
        "splitTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4770000000000004",
        "orderNo": "OB20261100064",
        "newOrderNos": "OB20261100064-1,OB20261100064-2",
        "customerCode": "C004",
        "splitReason": "超重",
        "operator": "示例员工1",
        "splitTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4770000000000005",
        "orderNo": "OB20261100065",
        "newOrderNos": "OB20261100065-1,OB20261100065-2",
        "customerCode": "C005",
        "splitReason": "缺货",
        "operator": "示例员工2",
        "splitTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "swapOrder",
    "route": "outbound/swapOrder",
    "title": "换单记录",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "oldTrackingNo",
        "label": "原跟踪号",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "newTrackingNo",
        "label": "新跟踪号",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "reason",
        "label": "换单原因",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "待换单",
            "warning"
          ],
          "20": [
            "已换单",
            "success"
          ],
          "30": [
            "失败",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ]
    },
    "mock": [
      {
        "id": "4780000000000001",
        "orderNo": "OB20261100081",
        "oldTrackingNo": "TRK00300000",
        "newTrackingNo": "",
        "channelName": "示例渠道1",
        "reason": "面单破损",
        "status": 10,
        "statusName": "待换单",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4780000000000002",
        "orderNo": "OB20261100082",
        "oldTrackingNo": "TRK00300009",
        "newTrackingNo": "TRK00400009",
        "channelName": "示例渠道2",
        "reason": "渠道变更",
        "status": 20,
        "statusName": "已换单",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4780000000000003",
        "orderNo": "OB20261100083",
        "oldTrackingNo": "TRK00300018",
        "newTrackingNo": "TRK00400018",
        "channelName": "示例渠道3",
        "reason": "地址修改",
        "status": 20,
        "statusName": "已换单",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4780000000000004",
        "orderNo": "OB20261100084",
        "oldTrackingNo": "TRK00300027",
        "newTrackingNo": "",
        "channelName": "示例渠道4",
        "reason": "面单破损",
        "status": 30,
        "statusName": "失败",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4780000000000005",
        "orderNo": "OB20261100085",
        "oldTrackingNo": "TRK00300036",
        "newTrackingNo": "",
        "channelName": "示例渠道1",
        "reason": "渠道变更",
        "status": 10,
        "statusName": "待换单",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "rebrand",
    "route": "returnGoods/rebrand",
    "title": "换标任务",
    "columns": [
      {
        "name": "taskNo",
        "label": "任务号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "sku",
        "label": "SKU",
        "type": "str",
        "width": 110,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "qty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "newBarcode",
        "label": "新条码",
        "type": "str",
        "width": 130,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
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
            "待处理",
            "info"
          ],
          "20": [
            "处理中",
            "warning"
          ],
          "30": [
            "已完成",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4790000000000001",
        "taskNo": "RB20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "sku": "SKU-0001",
        "qty": 1,
        "newBarcode": "NB00700000",
        "status": 10,
        "statusName": "待处理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4790000000000002",
        "taskNo": "RB20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "sku": "SKU-0002",
        "qty": 2,
        "newBarcode": "NB00700003",
        "status": 20,
        "statusName": "处理中",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4790000000000003",
        "taskNo": "RB20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "sku": "SKU-0003",
        "qty": 3,
        "newBarcode": "NB00700006",
        "status": 30,
        "statusName": "已完成",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4790000000000004",
        "taskNo": "RB20261100004",
        "customerCode": "C004",
        "whCode": "WH02",
        "sku": "SKU-0004",
        "qty": 4,
        "newBarcode": "NB00700009",
        "status": 10,
        "statusName": "待处理",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4790000000000005",
        "taskNo": "RB20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "sku": "SKU-0005",
        "qty": 5,
        "newBarcode": "NB00700012",
        "status": 20,
        "statusName": "处理中",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "returnOutbound",
    "route": "returnGoods/outbound",
    "title": "退货出库",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "returnNo",
        "label": "退货单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "skuQty",
        "label": "数量",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "outboundType",
        "label": "出库类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待出库",
            "warning"
          ],
          "20": [
            "已出库",
            "success"
          ],
          "90": [
            "已取消",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ],
      "outboundType": [
        "转仓",
        "退回客户",
        "销毁"
      ]
    },
    "mock": [
      {
        "id": "4800000000000001",
        "orderNo": "RO20261100001",
        "returnNo": "RT20261100001",
        "customerCode": "C001",
        "whCode": "WH01",
        "skuQty": 1,
        "outboundType": "退回客户",
        "status": 10,
        "statusName": "待出库",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4800000000000002",
        "orderNo": "RO20261100002",
        "returnNo": "RT20261100002",
        "customerCode": "C002",
        "whCode": "WH02",
        "skuQty": 2,
        "outboundType": "销毁",
        "status": 20,
        "statusName": "已出库",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4800000000000003",
        "orderNo": "RO20261100003",
        "returnNo": "RT20261100003",
        "customerCode": "C003",
        "whCode": "WH01",
        "skuQty": 3,
        "outboundType": "转仓",
        "status": 20,
        "statusName": "已出库",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4800000000000004",
        "orderNo": "RO20261100004",
        "returnNo": "RT20261100004",
        "customerCode": "C004",
        "whCode": "WH02",
        "skuQty": 4,
        "outboundType": "退回客户",
        "status": 90,
        "statusName": "已取消",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4800000000000005",
        "orderNo": "RO20261100005",
        "returnNo": "RT20261100005",
        "customerCode": "C005",
        "whCode": "WH01",
        "skuQty": 1,
        "outboundType": "销毁",
        "status": 10,
        "statusName": "待出库",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "fuelPlan",
    "route": "logisticsPrice/fuelPlan",
    "title": "燃油费方案",
    "columns": [
      {
        "name": "planCode",
        "label": "方案编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "planName",
        "label": "方案名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "fuelRate",
        "label": "燃油费率(%)",
        "type": "num",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "effectiveDate",
        "label": "生效日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "expireDate",
        "label": "失效日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "planCode",
    "tabs": "status",
    "filterOptions": {
      "carrier": [
        "示例承运商A",
        "示例承运商B",
        "示例承运商C"
      ]
    },
    "mock": [
      {
        "id": "4810000000000001",
        "planCode": "FP-001",
        "planName": "示例燃油方案1",
        "carrier": "示例承运商A",
        "fuelRate": 12,
        "effectiveDate": "2026-11-01",
        "expireDate": "2026-12-31",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4810000000000002",
        "planCode": "FP-002",
        "planName": "示例燃油方案2",
        "carrier": "示例承运商B",
        "fuelRate": 12.75,
        "effectiveDate": "2026-11-02",
        "expireDate": "2026-12-31",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4810000000000003",
        "planCode": "FP-003",
        "planName": "示例燃油方案3",
        "carrier": "示例承运商C",
        "fuelRate": 13.5,
        "effectiveDate": "2026-11-03",
        "expireDate": "2026-12-31",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4810000000000004",
        "planCode": "FP-004",
        "planName": "示例燃油方案4",
        "carrier": "示例承运商A",
        "fuelRate": 14.25,
        "effectiveDate": "2026-11-04",
        "expireDate": "2026-12-31",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4810000000000005",
        "planCode": "FP-005",
        "planName": "示例燃油方案5",
        "carrier": "示例承运商B",
        "fuelRate": 15,
        "effectiveDate": "2026-11-05",
        "expireDate": "2026-12-31",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "zoneTemp",
    "route": "logisticsPrice/zoneTemp",
    "title": "分区模板",
    "columns": [
      {
        "name": "tempCode",
        "label": "模板编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "tempName",
        "label": "模板名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "country",
        "label": "国家",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "zoneCount",
        "label": "分区数",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "tempCode",
    "tabs": "status",
    "filterOptions": {
      "carrier": [
        "示例承运商A",
        "示例承运商B",
        "示例承运商C"
      ],
      "country": [
        "CA",
        "DE",
        "GB",
        "US"
      ]
    },
    "mock": [
      {
        "id": "4820000000000001",
        "tempCode": "ZT-001",
        "tempName": "示例分区模板1",
        "carrier": "示例承运商A",
        "country": "US",
        "zoneCount": 8,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4820000000000002",
        "tempCode": "ZT-002",
        "tempName": "示例分区模板2",
        "carrier": "示例承运商B",
        "country": "CA",
        "zoneCount": 9,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4820000000000003",
        "tempCode": "ZT-003",
        "tempName": "示例分区模板3",
        "carrier": "示例承运商C",
        "country": "GB",
        "zoneCount": 12,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4820000000000004",
        "tempCode": "ZT-004",
        "tempName": "示例分区模板4",
        "carrier": "示例承运商A",
        "country": "DE",
        "zoneCount": 8,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4820000000000005",
        "tempCode": "ZT-005",
        "tempName": "示例分区模板5",
        "carrier": "示例承运商B",
        "country": "US",
        "zoneCount": 9,
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "surcharge",
    "route": "logisticsPrice/surcharge",
    "title": "附加费",
    "columns": [
      {
        "name": "chargeCode",
        "label": "费用编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "chargeName",
        "label": "费用名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "carrier",
        "label": "承运商",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "chargeType",
        "label": "计费方式",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "amount",
        "label": "金额",
        "type": "money",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "chargeCode",
    "tabs": "status",
    "filterOptions": {
      "carrier": [
        "示例承运商A",
        "示例承运商B",
        "示例承运商C"
      ],
      "chargeType": [
        "按件",
        "按票",
        "按重量"
      ],
      "currency": [
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4830000000000001",
        "chargeCode": "SC-001",
        "chargeName": "偏远附加费",
        "carrier": "示例承运商A",
        "chargeType": "按票",
        "amount": 2.5,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4830000000000002",
        "chargeCode": "SC-002",
        "chargeName": "超长附加费",
        "carrier": "示例承运商B",
        "chargeType": "按件",
        "amount": 4.3,
        "currency": "EUR",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4830000000000003",
        "chargeCode": "SC-003",
        "chargeName": "住宅地址费",
        "carrier": "示例承运商C",
        "chargeType": "按重量",
        "amount": 6.1,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4830000000000004",
        "chargeCode": "SC-004",
        "chargeName": "签名服务费",
        "carrier": "示例承运商A",
        "chargeType": "按票",
        "amount": 7.9,
        "currency": "EUR",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4830000000000005",
        "chargeCode": "SC-005",
        "chargeName": "超重附加费",
        "carrier": "示例承运商B",
        "chargeType": "按件",
        "amount": 9.7,
        "currency": "USD",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "freightTemp",
    "route": "logisticsPrice/freightTemp",
    "title": "运费模板",
    "columns": [
      {
        "name": "tempCode",
        "label": "模板编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "tempName",
        "label": "模板名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "billingMode",
        "label": "计费模式",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "tempCode",
    "tabs": "status",
    "filterOptions": {
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ],
      "billingMode": [
        "阶梯价",
        "首重续重"
      ],
      "currency": [
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4840000000000001",
        "tempCode": "FT-001",
        "tempName": "示例运费模板1",
        "channelName": "示例渠道1",
        "billingMode": "首重续重",
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4840000000000002",
        "tempCode": "FT-002",
        "tempName": "示例运费模板2",
        "channelName": "示例渠道2",
        "billingMode": "阶梯价",
        "currency": "EUR",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4840000000000003",
        "tempCode": "FT-003",
        "tempName": "示例运费模板3",
        "channelName": "示例渠道3",
        "billingMode": "首重续重",
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4840000000000004",
        "tempCode": "FT-004",
        "tempName": "示例运费模板4",
        "channelName": "示例渠道4",
        "billingMode": "阶梯价",
        "currency": "EUR",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4840000000000005",
        "tempCode": "FT-005",
        "tempName": "示例运费模板5",
        "channelName": "示例渠道1",
        "billingMode": "首重续重",
        "currency": "USD",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "logisticsPriceSetting",
    "route": "quotesReceivable/logisticsPriceSetting",
    "title": "物流报价设置",
    "columns": [
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": true,
        "filter": true,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "width": 110,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "freightTempName",
        "label": "运费模板",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "discountRate",
        "label": "折扣率",
        "type": "rate",
        "width": 100,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ]
    },
    "mock": [
      {
        "id": "4850000000000001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "channelName": "示例渠道1",
        "freightTempName": "示例运费模板1",
        "discountRate": 0.8,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4850000000000002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "channelName": "示例渠道2",
        "freightTempName": "示例运费模板2",
        "discountRate": 0.85,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4850000000000003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "channelName": "示例渠道3",
        "freightTempName": "示例运费模板3",
        "discountRate": 0.9,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4850000000000004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "channelName": "示例渠道4",
        "freightTempName": "示例运费模板4",
        "discountRate": 0.95,
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4850000000000005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "channelName": "示例渠道1",
        "freightTempName": "示例运费模板5",
        "discountRate": 1,
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "operateBill",
    "route": "quotesReceivable/operateBill",
    "title": "操作费报价",
    "columns": [
      {
        "name": "itemCode",
        "label": "费用项编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "itemName",
        "label": "费用项",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "billingUnit",
        "label": "计费单位",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "unitPrice",
        "label": "单价",
        "type": "money",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "itemCode",
    "tabs": "status",
    "filterOptions": {
      "billingUnit": [
        "件",
        "托",
        "票",
        "箱"
      ],
      "currency": [
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4860000000000001",
        "itemCode": "OP-001",
        "itemName": "入库卸货",
        "billingUnit": "票",
        "unitPrice": 0.3,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4860000000000002",
        "itemCode": "OP-002",
        "itemName": "上架",
        "billingUnit": "件",
        "unitPrice": 0.75,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4860000000000003",
        "itemCode": "OP-003",
        "itemName": "拣货",
        "billingUnit": "箱",
        "unitPrice": 1.2,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4860000000000004",
        "itemCode": "OP-004",
        "itemName": "打包",
        "billingUnit": "托",
        "unitPrice": 1.65,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4860000000000005",
        "itemCode": "OP-005",
        "itemName": "贴标",
        "billingUnit": "票",
        "unitPrice": 2.1,
        "currency": "USD",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "warehouseRent",
    "route": "quotesReceivable/warehouseRent",
    "title": "仓租报价",
    "columns": [
      {
        "name": "planCode",
        "label": "方案编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "planName",
        "label": "方案名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "freeDays",
        "label": "免租天数",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "unitPrice",
        "label": "单价(每立方米/天)",
        "type": "money",
        "width": 140,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "planCode",
    "tabs": "status",
    "filterOptions": {
      "whCode": [
        "WH01",
        "WH02"
      ],
      "currency": [
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4870000000000001",
        "planCode": "WR-001",
        "planName": "示例仓租方案1",
        "whCode": "WH01",
        "freeDays": 30,
        "unitPrice": 0.2,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4870000000000002",
        "planCode": "WR-002",
        "planName": "示例仓租方案2",
        "whCode": "WH02",
        "freeDays": 60,
        "unitPrice": 0.25,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4870000000000003",
        "planCode": "WR-003",
        "planName": "示例仓租方案3",
        "whCode": "WH01",
        "freeDays": 90,
        "unitPrice": 0.3,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4870000000000004",
        "planCode": "WR-004",
        "planName": "示例仓租方案4",
        "whCode": "WH02",
        "freeDays": 30,
        "unitPrice": 0.35,
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4870000000000005",
        "planCode": "WR-005",
        "planName": "示例仓租方案5",
        "whCode": "WH01",
        "freeDays": 60,
        "unitPrice": 0.4,
        "currency": "USD",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "quotationScheme",
    "route": "quotesReceivable/quotationScheme",
    "title": "报价方案",
    "columns": [
      {
        "name": "schemeCode",
        "label": "方案编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "schemeName",
        "label": "方案名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCount",
        "label": "适用客户数",
        "type": "int",
        "width": 100,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "effectiveDate",
        "label": "生效日期",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "草稿",
            "info"
          ],
          "1": [
            "生效",
            "success"
          ],
          "2": [
            "失效",
            "warning"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "schemeCode",
    "tabs": "status",
    "filterOptions": {
      "currency": [
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4880000000000001",
        "schemeCode": "QS-001",
        "schemeName": "示例报价方案1",
        "customerCount": 0,
        "currency": "USD",
        "effectiveDate": "2026-11-01",
        "status": 1,
        "statusName": "生效",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4880000000000002",
        "schemeCode": "QS-002",
        "schemeName": "示例报价方案2",
        "customerCount": 1,
        "currency": "EUR",
        "effectiveDate": "2026-11-02",
        "status": 1,
        "statusName": "生效",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4880000000000003",
        "schemeCode": "QS-003",
        "schemeName": "示例报价方案3",
        "customerCount": 2,
        "currency": "USD",
        "effectiveDate": "2026-11-03",
        "status": 0,
        "statusName": "草稿",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4880000000000004",
        "schemeCode": "QS-004",
        "schemeName": "示例报价方案4",
        "customerCount": 3,
        "currency": "EUR",
        "effectiveDate": "2026-11-04",
        "status": 2,
        "statusName": "失效",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4880000000000005",
        "schemeCode": "QS-005",
        "schemeName": "示例报价方案5",
        "customerCount": 0,
        "currency": "USD",
        "effectiveDate": "2026-11-05",
        "status": 1,
        "statusName": "生效",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "businessExpenses",
    "route": "receivableSettlement/businessExpenses",
    "title": "业务费用",
    "columns": [
      {
        "name": "feeNo",
        "label": "费用单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "bizNo",
        "label": "业务单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "feeType",
        "label": "费用类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "amount",
        "label": "金额",
        "type": "money",
        "width": 100,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "未出账",
            "warning"
          ],
          "20": [
            "已出账",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "feeTime",
        "label": "计费时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "feeType": [
        "仓租",
        "操作费",
        "运费",
        "附加费"
      ],
      "currency": [
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4890000000000001",
        "feeNo": "FE202611000001",
        "bizNo": "OB20261100001",
        "customerCode": "C001",
        "feeType": "操作费",
        "amount": 3.2,
        "currency": "USD",
        "status": 10,
        "statusName": "未出账",
        "feeTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4890000000000002",
        "feeNo": "FE202611000002",
        "bizNo": "ASN20261100002",
        "customerCode": "C002",
        "feeType": "仓租",
        "amount": 7.35,
        "currency": "USD",
        "status": 20,
        "statusName": "已出账",
        "feeTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4890000000000003",
        "feeNo": "FE202611000003",
        "bizNo": "FO20261100003",
        "customerCode": "C003",
        "feeType": "运费",
        "amount": 11.5,
        "currency": "USD",
        "status": 20,
        "statusName": "已出账",
        "feeTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4890000000000004",
        "feeNo": "FE202611000004",
        "bizNo": "OB20261100004",
        "customerCode": "C004",
        "feeType": "附加费",
        "amount": 15.65,
        "currency": "USD",
        "status": 10,
        "statusName": "未出账",
        "feeTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4890000000000005",
        "feeNo": "FE202611000005",
        "bizNo": "ASN20261100005",
        "customerCode": "C005",
        "feeType": "操作费",
        "amount": 19.8,
        "currency": "USD",
        "status": 20,
        "statusName": "已出账",
        "feeTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 18
  },
  {
    "key": "billsReceivable",
    "route": "receivableSettlement/billsReceivable",
    "title": "应收账单",
    "columns": [
      {
        "name": "billNo",
        "label": "账单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "customerName",
        "label": "客户名称",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "billPeriod",
        "label": "账期",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "amount",
        "label": "账单金额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
            "待确认",
            "warning"
          ],
          "20": [
            "已确认",
            "primary"
          ],
          "30": [
            "已结清",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "billPeriod": [
        "2026-09",
        "2026-10",
        "2026-11"
      ],
      "currency": [
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4900000000000001",
        "billNo": "BL20261100001",
        "customerCode": "C001",
        "customerName": "示例客户1",
        "billPeriod": "2026-09",
        "amount": 800,
        "currency": "USD",
        "status": 30,
        "statusName": "已结清",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4900000000000002",
        "billNo": "BL20261100002",
        "customerCode": "C002",
        "customerName": "示例客户2",
        "billPeriod": "2026-09",
        "amount": 1015.3,
        "currency": "USD",
        "status": 30,
        "statusName": "已结清",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4900000000000003",
        "billNo": "BL20261100003",
        "customerCode": "C003",
        "customerName": "示例客户3",
        "billPeriod": "2026-09",
        "amount": 1230.6,
        "currency": "USD",
        "status": 30,
        "statusName": "已结清",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4900000000000004",
        "billNo": "BL20261100004",
        "customerCode": "C004",
        "customerName": "示例客户4",
        "billPeriod": "2026-09",
        "amount": 1445.9,
        "currency": "USD",
        "status": 30,
        "statusName": "已结清",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4900000000000005",
        "billNo": "BL20261100005",
        "customerCode": "C005",
        "customerName": "示例客户5",
        "billPeriod": "2026-10",
        "amount": 1661.2,
        "currency": "USD",
        "status": 20,
        "statusName": "已确认",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "expend",
    "route": "receivableSettlement/expend",
    "title": "扣款记录",
    "columns": [
      {
        "name": "expendNo",
        "label": "扣款单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "billNo",
        "label": "关联账单",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "amount",
        "label": "扣款金额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "balanceAfter",
        "label": "扣后余额",
        "type": "money",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "expendTime",
        "label": "扣款时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": null,
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "currency": [
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4910000000000001",
        "expendNo": "EX20261100001",
        "customerCode": "C001",
        "billNo": "BL20261100001",
        "amount": 50,
        "currency": "USD",
        "balanceAfter": 5000,
        "expendTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4910000000000002",
        "expendNo": "EX20261100002",
        "customerCode": "C002",
        "billNo": "BL20261100002",
        "amount": 83.3,
        "currency": "USD",
        "balanceAfter": 4879.5,
        "expendTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4910000000000003",
        "expendNo": "EX20261100003",
        "customerCode": "C003",
        "billNo": "BL20261100003",
        "amount": 116.6,
        "currency": "USD",
        "balanceAfter": 4759,
        "expendTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4910000000000004",
        "expendNo": "EX20261100004",
        "customerCode": "C004",
        "billNo": "BL20261100004",
        "amount": 149.9,
        "currency": "USD",
        "balanceAfter": 4638.5,
        "expendTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4910000000000005",
        "expendNo": "EX20261100005",
        "customerCode": "C005",
        "billNo": "BL20261100005",
        "amount": 183.2,
        "currency": "USD",
        "balanceAfter": 4518,
        "expendTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "billTemplate",
    "route": "receivableSettlement/billTemplate",
    "title": "账单模板",
    "columns": [
      {
        "name": "tempCode",
        "label": "模板编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "tempName",
        "label": "模板名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "billCycle",
        "label": "出账周期",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "feeTypes",
        "label": "包含费用",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "tempCode",
    "tabs": "status",
    "filterOptions": {
      "billCycle": [
        "半月结",
        "周结",
        "月结"
      ]
    },
    "mock": [
      {
        "id": "4920000000000001",
        "tempCode": "BT-001",
        "tempName": "示例账单模板1",
        "billCycle": "月结",
        "feeTypes": "操作费,仓租",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4920000000000002",
        "tempCode": "BT-002",
        "tempName": "示例账单模板2",
        "billCycle": "半月结",
        "feeTypes": "操作费,仓租,运费",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4920000000000003",
        "tempCode": "BT-003",
        "tempName": "示例账单模板3",
        "billCycle": "周结",
        "feeTypes": "全部",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4920000000000004",
        "tempCode": "BT-004",
        "tempName": "示例账单模板4",
        "billCycle": "月结",
        "feeTypes": "操作费,仓租",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4920000000000005",
        "tempCode": "BT-005",
        "tempName": "示例账单模板5",
        "billCycle": "半月结",
        "feeTypes": "操作费,仓租,运费",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 6
  },
  {
    "key": "printTemplate",
    "route": "setting/printTemplate",
    "title": "打印模板",
    "columns": [
      {
        "name": "tempCode",
        "label": "模板编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "tempName",
        "label": "模板名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "tempType",
        "label": "模板类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "paperSize",
        "label": "纸张",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "isDefault",
        "label": "默认",
        "type": "int",
        "width": 70,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "否",
            "info"
          ],
          "1": [
            "是",
            "success"
          ]
        },
        "required": true,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "tempCode",
    "tabs": "status",
    "filterOptions": {
      "tempType": [
        "SKU标签",
        "拣货单",
        "箱唛",
        "面单"
      ],
      "paperSize": [
        "100x100",
        "100x150",
        "A4"
      ]
    },
    "mock": [
      {
        "id": "4930000000000001",
        "tempCode": "PT-001",
        "tempName": "示例打印模板1",
        "tempType": "面单",
        "paperSize": "100x100",
        "isDefault": 1,
        "isDefaultName": "是",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4930000000000002",
        "tempCode": "PT-002",
        "tempName": "示例打印模板2",
        "tempType": "箱唛",
        "paperSize": "100x150",
        "isDefault": 1,
        "isDefaultName": "是",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4930000000000003",
        "tempCode": "PT-003",
        "tempName": "示例打印模板3",
        "tempType": "拣货单",
        "paperSize": "A4",
        "isDefault": 1,
        "isDefaultName": "是",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4930000000000004",
        "tempCode": "PT-004",
        "tempName": "示例打印模板4",
        "tempType": "SKU标签",
        "paperSize": "100x100",
        "isDefault": 1,
        "isDefaultName": "是",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4930000000000005",
        "tempCode": "PT-005",
        "tempName": "示例打印模板5",
        "tempType": "面单",
        "paperSize": "100x150",
        "isDefault": 0,
        "isDefaultName": "否",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 10
  },
  {
    "key": "brand",
    "route": "setting/brand",
    "title": "品牌设置",
    "columns": [
      {
        "name": "brandCode",
        "label": "品牌编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "brandName",
        "label": "品牌名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "domain",
        "label": "访问域名",
        "type": "str",
        "width": 200,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "brandCode",
    "tabs": "status",
    "filterOptions": {},
    "mock": [
      {
        "id": "4940000000000001",
        "brandCode": "BR-001",
        "brandName": "示例品牌1",
        "domain": "wms-demo-1.example.com",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4940000000000002",
        "brandCode": "BR-002",
        "brandName": "示例品牌2",
        "domain": "wms-demo-2.example.com",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4940000000000003",
        "brandCode": "BR-003",
        "brandName": "示例品牌3",
        "domain": "wms-demo-3.example.com",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4940000000000004",
        "brandCode": "BR-004",
        "brandName": "示例品牌4",
        "domain": "wms-demo-4.example.com",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-04 12:21:00"
      }
    ],
    "mockTotal": 4
  },
  {
    "key": "agency",
    "route": "setting/agency",
    "title": "服务商",
    "columns": [
      {
        "name": "agencyCode",
        "label": "服务商编码",
        "type": "str",
        "width": 120,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "agencyName",
        "label": "服务商名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "agencyType",
        "label": "类型",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "contactName",
        "label": "联系人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "agencyCode",
    "tabs": "status",
    "filterOptions": {
      "agencyType": [
        "报关行",
        "海外仓合作方",
        "物流服务商"
      ]
    },
    "mock": [
      {
        "id": "4950000000000001",
        "agencyCode": "AG-001",
        "agencyName": "示例服务商1",
        "agencyType": "物流服务商",
        "contactName": "示例联系人1",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4950000000000002",
        "agencyCode": "AG-002",
        "agencyName": "示例服务商2",
        "agencyType": "报关行",
        "contactName": "示例联系人2",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4950000000000003",
        "agencyCode": "AG-003",
        "agencyName": "示例服务商3",
        "agencyType": "海外仓合作方",
        "contactName": "示例联系人3",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4950000000000004",
        "agencyCode": "AG-004",
        "agencyName": "示例服务商4",
        "agencyType": "物流服务商",
        "contactName": "示例联系人4",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4950000000000005",
        "agencyCode": "AG-005",
        "agencyName": "示例服务商5",
        "agencyType": "报关行",
        "contactName": "示例联系人5",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "erpAuth",
    "route": "setting/erpAuth",
    "title": "ERP 授权",
    "columns": [
      {
        "name": "erpName",
        "label": "ERP",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": true,
        "filter": true,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "appKeyMasked",
        "label": "AppKey（脱敏）",
        "type": "str",
        "width": 140,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "authTime",
        "label": "授权时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      },
      {
        "name": "expireTime",
        "label": "到期时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
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
            "已过期",
            "info"
          ],
          "1": [
            "有效",
            "success"
          ]
        },
        "required": true,
        "auto": null
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "erpName": [
        "示例ERP-A",
        "示例ERP-B",
        "示例ERP-C"
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
        "id": "4960000000000001",
        "erpName": "示例ERP-A",
        "customerCode": "C001",
        "appKeyMasked": "demo_****0001",
        "authTime": "2026-11-01 09:00:00",
        "expireTime": "2027-11-01 00:00:00",
        "status": 1,
        "statusName": "有效"
      },
      {
        "id": "4960000000000002",
        "erpName": "示例ERP-B",
        "customerCode": "C002",
        "appKeyMasked": "demo_****0002",
        "authTime": "2026-11-02 10:07:00",
        "expireTime": "2027-11-01 00:00:00",
        "status": 1,
        "statusName": "有效"
      },
      {
        "id": "4960000000000003",
        "erpName": "示例ERP-C",
        "customerCode": "C003",
        "appKeyMasked": "demo_****0003",
        "authTime": "2026-11-03 11:14:00",
        "expireTime": "2027-11-01 00:00:00",
        "status": 1,
        "statusName": "有效"
      },
      {
        "id": "4960000000000004",
        "erpName": "示例ERP-A",
        "customerCode": "C004",
        "appKeyMasked": "demo_****0004",
        "authTime": "2026-11-04 12:21:00",
        "expireTime": "2026-11-20 00:00:00",
        "status": 0,
        "statusName": "已过期"
      },
      {
        "id": "4960000000000005",
        "erpName": "示例ERP-B",
        "customerCode": "C005",
        "appKeyMasked": "demo_****0005",
        "authTime": "2026-11-05 13:28:00",
        "expireTime": "2027-11-01 00:00:00",
        "status": 1,
        "statusName": "有效"
      }
    ],
    "mockTotal": 8
  },
  {
    "key": "msgNotice",
    "route": "setting/msgNotice",
    "title": "消息通知",
    "columns": [
      {
        "name": "title",
        "label": "标题",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "msgType",
        "label": "消息类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "receiver",
        "label": "接收人",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "readFlag",
        "label": "状态",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "未读",
            "warning"
          ],
          "1": [
            "已读",
            "info"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "sendTime",
        "label": "发送时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "readFlag",
    "filterOptions": {
      "msgType": [
        "库存",
        "系统",
        "订单",
        "账单"
      ]
    },
    "mock": [
      {
        "id": "4970000000000001",
        "title": "示例系统消息1",
        "msgType": "系统",
        "receiver": "示例员工1",
        "readFlag": 0,
        "readFlagName": "未读",
        "sendTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4970000000000002",
        "title": "示例订单消息2",
        "msgType": "订单",
        "receiver": "示例员工2",
        "readFlag": 1,
        "readFlagName": "已读",
        "sendTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4970000000000003",
        "title": "示例库存消息3",
        "msgType": "库存",
        "receiver": "示例员工3",
        "readFlag": 1,
        "readFlagName": "已读",
        "sendTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4970000000000004",
        "title": "示例账单消息4",
        "msgType": "账单",
        "receiver": "示例员工4",
        "readFlag": 0,
        "readFlagName": "未读",
        "sendTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4970000000000005",
        "title": "示例系统消息5",
        "msgType": "系统",
        "receiver": "示例员工1",
        "readFlag": 1,
        "readFlagName": "已读",
        "sendTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "warningMsg",
    "route": "setting/warningMsg",
    "title": "预警消息",
    "columns": [
      {
        "name": "warningType",
        "label": "预警类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "content",
        "label": "内容",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "whCode",
        "label": "仓库",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "level",
        "label": "级别",
        "type": "int",
        "width": 80,
        "kw": false,
        "filter": false,
        "enum": {
          "1": [
            "提示",
            "info"
          ],
          "2": [
            "警告",
            "warning"
          ],
          "3": [
            "严重",
            "danger"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "handled",
        "label": "处理状态",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": {
          "0": [
            "未处理",
            "warning"
          ],
          "1": [
            "已处理",
            "success"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "level",
    "filterOptions": {
      "warningType": [
        "库存不足",
        "库龄超期",
        "订单超时"
      ],
      "whCode": [
        "WH01",
        "WH02"
      ]
    },
    "mock": [
      {
        "id": "4980000000000001",
        "warningType": "库存不足",
        "content": "示例预警：库存不足 SKU-0001",
        "whCode": "WH01",
        "level": 1,
        "levelName": "提示",
        "handled": 0,
        "handledName": "未处理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4980000000000002",
        "warningType": "库龄超期",
        "content": "示例预警：库龄超期 SKU-0002",
        "whCode": "WH02",
        "level": 2,
        "levelName": "警告",
        "handled": 1,
        "handledName": "已处理",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4980000000000003",
        "warningType": "订单超时",
        "content": "示例预警：订单超时 SKU-0003",
        "whCode": "WH01",
        "level": 3,
        "levelName": "严重",
        "handled": 0,
        "handledName": "未处理",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4980000000000004",
        "warningType": "库存不足",
        "content": "示例预警：库存不足 SKU-0004",
        "whCode": "WH02",
        "level": 2,
        "levelName": "警告",
        "handled": 1,
        "handledName": "已处理",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4980000000000005",
        "warningType": "库龄超期",
        "content": "示例预警：库龄超期 SKU-0005",
        "whCode": "WH01",
        "level": 1,
        "levelName": "提示",
        "handled": 0,
        "handledName": "未处理",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 12
  },
  {
    "key": "receiveAccount",
    "route": "setting/receiveAccount",
    "title": "收款账户",
    "columns": [
      {
        "name": "accountName",
        "label": "账户名称",
        "type": "str",
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "bankName",
        "label": "开户行",
        "type": "str",
        "width": 120,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "accountNoMasked",
        "label": "账号（脱敏）",
        "type": "str",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "currency",
        "label": "币种",
        "type": "str",
        "width": 70,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "updateTime",
        "label": "更新时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "update"
      }
    ],
    "editable": true,
    "unique": "accountName",
    "tabs": "status",
    "filterOptions": {
      "bankName": [
        "示例银行A",
        "示例银行B"
      ],
      "currency": [
        "CNY",
        "EUR",
        "USD"
      ]
    },
    "mock": [
      {
        "id": "4990000000000001",
        "accountName": "示例收款账户1",
        "bankName": "示例银行A",
        "accountNoMasked": "**** **** **** 0001",
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-01 09:00:00"
      },
      {
        "id": "4990000000000002",
        "accountName": "示例收款账户2",
        "bankName": "示例银行B",
        "accountNoMasked": "**** **** **** 0002",
        "currency": "CNY",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-02 10:07:00"
      },
      {
        "id": "4990000000000003",
        "accountName": "示例收款账户3",
        "bankName": "示例银行A",
        "accountNoMasked": "**** **** **** 0003",
        "currency": "EUR",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-03 11:14:00"
      },
      {
        "id": "4990000000000004",
        "accountName": "示例收款账户4",
        "bankName": "示例银行B",
        "accountNoMasked": "**** **** **** 0004",
        "currency": "USD",
        "status": 1,
        "statusName": "启用",
        "updateTime": "2026-11-04 12:21:00"
      },
      {
        "id": "4990000000000005",
        "accountName": "示例收款账户5",
        "bankName": "示例银行A",
        "accountNoMasked": "**** **** **** 0005",
        "currency": "CNY",
        "status": 0,
        "statusName": "停用",
        "updateTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 5
  },
  {
    "key": "errorPool",
    "route": "toolKit/errorPool",
    "title": "异常池",
    "columns": [
      {
        "name": "errorNo",
        "label": "异常编号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "bizType",
        "label": "业务类型",
        "type": "str",
        "width": 90,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "bizNo",
        "label": "业务单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "errorMsg",
        "label": "异常信息",
        "type": "str",
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "retryCount",
        "label": "重试次数",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
            "已解决",
            "success"
          ],
          "2": [
            "已忽略",
            "info"
          ]
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "status",
    "filterOptions": {
      "bizType": [
        "入库单",
        "出库单",
        "推送"
      ]
    },
    "mock": [
      {
        "id": "5000000000000001",
        "errorNo": "ERR202611000001",
        "bizType": "出库单",
        "bizNo": "OB20261100001",
        "errorMsg": "示例：渠道下单失败",
        "retryCount": 0,
        "status": 0,
        "statusName": "待处理",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "5000000000000002",
        "errorNo": "ERR202611000002",
        "bizType": "入库单",
        "bizNo": "ASN20261100002",
        "errorMsg": "示例：SKU 不存在",
        "retryCount": 1,
        "status": 1,
        "statusName": "已解决",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "5000000000000003",
        "errorNo": "ERR202611000003",
        "bizType": "推送",
        "bizNo": "PS20261100003",
        "errorMsg": "示例：回传超时",
        "retryCount": 2,
        "status": 1,
        "statusName": "已解决",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "5000000000000004",
        "errorNo": "ERR202611000004",
        "bizType": "出库单",
        "bizNo": "OB20261100004",
        "errorMsg": "示例：渠道下单失败",
        "retryCount": 3,
        "status": 2,
        "statusName": "已忽略",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "5000000000000005",
        "errorNo": "ERR202611000005",
        "bizType": "入库单",
        "bizNo": "ASN20261100005",
        "errorMsg": "示例：SKU 不存在",
        "retryCount": 0,
        "status": 0,
        "statusName": "待处理",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "labelRecord",
    "route": "toolKit/labelRecord",
    "title": "面单记录",
    "columns": [
      {
        "name": "orderNo",
        "label": "出库单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "trackingNo",
        "label": "跟踪号",
        "type": "str",
        "width": 140,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "channelName",
        "label": "物流渠道",
        "type": "str",
        "width": 110,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "labelFormat",
        "label": "格式",
        "type": "str",
        "width": 80,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "createTime",
        "label": "创建时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "create"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "result",
    "filterOptions": {
      "channelName": [
        "示例渠道1",
        "示例渠道2",
        "示例渠道3",
        "示例渠道4"
      ],
      "labelFormat": [
        "PDF",
        "PNG",
        "ZPL"
      ]
    },
    "mock": [
      {
        "id": "5010000000000001",
        "orderNo": "OB20261100101",
        "trackingNo": "TRK00600000",
        "channelName": "示例渠道1",
        "labelFormat": "PDF",
        "result": 1,
        "resultName": "成功",
        "createTime": "2026-11-01 09:00:00"
      },
      {
        "id": "5010000000000002",
        "orderNo": "OB20261100102",
        "trackingNo": "TRK00600007",
        "channelName": "示例渠道2",
        "labelFormat": "PNG",
        "result": 1,
        "resultName": "成功",
        "createTime": "2026-11-02 10:07:00"
      },
      {
        "id": "5010000000000003",
        "orderNo": "OB20261100103",
        "trackingNo": "TRK00600014",
        "channelName": "示例渠道3",
        "labelFormat": "ZPL",
        "result": 1,
        "resultName": "成功",
        "createTime": "2026-11-03 11:14:00"
      },
      {
        "id": "5010000000000004",
        "orderNo": "OB20261100104",
        "trackingNo": "TRK00600021",
        "channelName": "示例渠道4",
        "labelFormat": "PDF",
        "result": 1,
        "resultName": "成功",
        "createTime": "2026-11-04 12:21:00"
      },
      {
        "id": "5010000000000005",
        "orderNo": "OB20261100105",
        "trackingNo": "",
        "channelName": "示例渠道1",
        "labelFormat": "PNG",
        "result": 0,
        "resultName": "失败",
        "createTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 14
  },
  {
    "key": "pushRecord",
    "route": "toolKit/pushRecord",
    "title": "推送记录",
    "columns": [
      {
        "name": "pushNo",
        "label": "推送编号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "bizNo",
        "label": "业务单号",
        "type": "str",
        "width": 150,
        "kw": true,
        "filter": false,
        "enum": null,
        "required": true,
        "auto": null
      },
      {
        "name": "customerCode",
        "label": "客户代码",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "pushType",
        "label": "推送类型",
        "type": "str",
        "width": 100,
        "kw": false,
        "filter": true,
        "enum": null,
        "required": false,
        "auto": null
      },
      {
        "name": "costMs",
        "label": "耗时(ms)",
        "type": "int",
        "width": 90,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": null
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
        },
        "required": true,
        "auto": null
      },
      {
        "name": "pushTime",
        "label": "推送时间",
        "type": "time",
        "width": 160,
        "kw": false,
        "filter": false,
        "enum": null,
        "required": false,
        "auto": "none"
      }
    ],
    "editable": false,
    "unique": null,
    "tabs": "result",
    "filterOptions": {
      "customerCode": [
        "C001",
        "C002",
        "C003",
        "C004",
        "C005"
      ],
      "pushType": [
        "入库回传",
        "出库回传",
        "库存同步"
      ]
    },
    "mock": [
      {
        "id": "5020000000000001",
        "pushNo": "PS202611000001",
        "bizNo": "OB20261100001",
        "customerCode": "C001",
        "pushType": "出库回传",
        "costMs": 60,
        "result": 1,
        "resultName": "成功",
        "pushTime": "2026-11-01 09:00:00"
      },
      {
        "id": "5020000000000002",
        "pushNo": "PS202611000002",
        "bizNo": "ASN20261100002",
        "customerCode": "C002",
        "pushType": "入库回传",
        "costMs": 113,
        "result": 1,
        "resultName": "成功",
        "pushTime": "2026-11-02 10:07:00"
      },
      {
        "id": "5020000000000003",
        "pushNo": "PS202611000003",
        "bizNo": "STK20261100003",
        "customerCode": "C003",
        "pushType": "库存同步",
        "costMs": 166,
        "result": 1,
        "resultName": "成功",
        "pushTime": "2026-11-03 11:14:00"
      },
      {
        "id": "5020000000000004",
        "pushNo": "PS202611000004",
        "bizNo": "OB20261100004",
        "customerCode": "C004",
        "pushType": "出库回传",
        "costMs": 219,
        "result": 1,
        "resultName": "成功",
        "pushTime": "2026-11-04 12:21:00"
      },
      {
        "id": "5020000000000005",
        "pushNo": "PS202611000005",
        "bizNo": "ASN20261100005",
        "customerCode": "C005",
        "pushType": "入库回传",
        "costMs": 272,
        "result": 1,
        "resultName": "成功",
        "pushTime": "2026-11-05 13:28:00"
      }
    ],
    "mockTotal": 16
  }
]
