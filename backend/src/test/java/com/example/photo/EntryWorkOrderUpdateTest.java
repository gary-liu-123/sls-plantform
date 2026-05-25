package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntryWorkOrderUpdateTest {

    private static final long DATA_ID = 162352692L;

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void updateEntryWorkOrder() throws Exception {
        String fieldDataListJson = """
                [
                 			{
                 				"fieldApiName": "name",
                 				"fieldTypeApiName": "field_type_auto_number",
                 				"fieldValue": "20260512-0001"
                 			},
                 			{
                 				"fieldApiName": "recruitChannel",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "劳务外包",
                 				"optionNameList": [
                 					"劳务外包"
                 				]
                 			},
                 			{
                 				"fieldApiName": "recruitChannelSub",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "重庆拓高人力资源管理有限公司",
                 				"optionNameList": [
                 					"重庆拓高人力资源管理有限公司"
                 				]
                 			},
                 			{
                 				"fieldApiName": "referrerStaff",
                 				"fieldTypeApiName": "field_type_staff",
                 				"fieldValue": "39657",
                 				"userEmail": "linchangbin@qq.com"
                 			},
                 			{
                 				"fieldApiName": "eventReason",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "劳务工转入",
                 				"optionNameList": [
                 					"劳务工转入"
                 				]
                 			},
                 			{
                 				"fieldApiName": "controlOrg",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "控股职能部门",
                 				"optionNameList": [
                 					"控股职能部门"
                 				]
                 			},
                 			{
                 				"fieldApiName": "employTypeCategory",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "外包工",
                 				"optionNameList": [
                 					"外包工"
                 				]
                 			},
                 			{
                 				"fieldApiName": "employType",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "外包工",
                 				"optionNameList": [
                 					"外包工"
                 				]
                 			},
                 			{
                 				"fieldApiName": "deptOrgDesc",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "庆小康控股有限公司/重庆小康工业集团股份有限公司/东风小康汽车有限公司/DFXK公司办公室"
                 			},
                 			{
                 				"fieldApiName": "postbak",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "入职预约核对事项",
                 				"optionNameList": [
                 					"入职预约核对事项"
                 				]
                 			},
                 			{
                 				"fieldApiName": "entryDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2026-05-13"
                 			},
                 			{
                 				"fieldApiName": "employer",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "重庆小康工业集团股份有限公司",
                 				"optionNameList": [
                 					"重庆小康工业集团股份有限公司"
                 				]
                 			},
                 			{
                 				"fieldApiName": "workAddress",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "重庆两江新区",
                 				"optionNameList": [
                 					"重庆两江新区"
                 				]
                 			},
                 			{
                 				"fieldApiName": "dispatchCompany",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "重庆云雷人力资源管理服务有限公司",
                 				"optionNameList": [
                 					"重庆云雷人力资源管理服务有限公司"
                 				]
                 			},
                 			{
                 				"fieldApiName": "directIndirectType",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "直接生产员工",
                 				"optionNameList": [
                 					"直接生产员工"
                 				]
                 			},
                 			{
                 				"fieldApiName": "jobRank",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "4级",
                 				"optionNameList": [
                 					"4级"
                 				]
                 			},
                 			{
                 				"fieldApiName": "jobLevel",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "3",
                 				"optionNameList": [
                 					"3"
                 				]
                 			},
                 			{
                 				"fieldApiName": "rankLevelMerge",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "4级-3"
                 			},
                 			{
                 				"fieldApiName": "outerWorkAgeMonth",
                 				"fieldTypeApiName": "field_type_numeric",
                 				"fieldValue": "48"
                 			},
                 			{
                 				"fieldApiName": "workUnit",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "重庆小康工业集团股份有限公司",
                 				"optionNameList": [
                 					"重庆小康工业集团股份有限公司"
                 				]
                 			},
                 			{
                 				"fieldApiName": "staffStatus",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "不在岗",
                 				"optionNameList": [
                 					"不在岗"
                 				]
                 			},
                 			{
                 				"fieldApiName": "probationMonth",
                 				"fieldTypeApiName": "field_type_numeric",
                 				"fieldValue": "36"
                 			},
                 			{
                 				"fieldApiName": "probationEndDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2026-08-13"
                 			},
                 			{
                 				"fieldApiName": "reportAddress",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "五云湖基地",
                 				"optionNameList": [
                 					"五云湖基地"
                 				]
                 			},
                 			{
                 				"fieldApiName": "staffNo",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "2026001001"
                 			},
                 			{
                 				"fieldApiName": "chineseName",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "杨俊平"
                 			},
                 			{
                 				"fieldApiName": "foreignName",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "yangjunping-Gary"
                 			},
                 			{
                 				"fieldApiName": "workStartDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2020-07-05"
                 			},
                 			{
                 				"fieldApiName": "personalPhonebak",
                 				"fieldTypeApiName": "field_type_telephone",
                 				"tagValueList": [
                 					{
                 						"tagName": "工作",
                 						"tagValue": "15651818750"
                 					}
                 				]
                 			},
                 			{
                 				"fieldApiName": "personalEmail",
                 				"fieldTypeApiName": "field_type_email",
                 				"tagValueList": [
                 					{
                 						"tagName": "工作",
                 						"tagValue": "yangjunping@udesk.cn"
                 					}
                 				]
                 			},
                 			{
                 				"fieldApiName": "nationality",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "中国大陆",
                 				"optionNameList": [
                 					"中国大陆"
                 				]
                 			},
                 			{
                 				"fieldApiName": "gender",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "男",
                 				"optionNameList": [
                 					"男"
                 				]
                 			},
                 			{
                 				"fieldApiName": "birthDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "1995-03-21"
                 			},
                 			{
                 				"fieldApiName": "maritalStatus",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "已婚",
                 				"optionNameList": [
                 					"已婚"
                 				]
                 			},
                 			{
                 				"fieldApiName": "marryDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2024-05-16"
                 			},
                 			{
                 				"fieldApiName": "nation",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "浙江省",
                 				"optionNameList": [
                 					"浙江省"
                 				]
                 			},
                 			{
                 				"fieldApiName": "householdType",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "外市城镇",
                 				"optionNameList": [
                 					"外市城镇"
                 				]
                 			},
                 			{
                 				"fieldApiName": "politicalStatus",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "中共党员",
                 				"optionNameList": [
                 					"中共党员"
                 				]
                 			},
                 			{
                 				"fieldApiName": "partyJoinDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2018-05-16"
                 			},
                 			{
                 				"fieldApiName": "drivingLicenseType",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "B1照",
                 				"optionNameList": [
                 					"B1照"
                 				]
                 			},
                 			{
                 				"fieldApiName": "healthStatus",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "健康",
                 				"optionNameList": [
                 					"健康"
                 				]
                 			},
                 			{
                 				"fieldApiName": "height",
                 				"fieldTypeApiName": "field_type_numeric",
                 				"fieldValue": "175"
                 			},
                 			{
                 				"fieldApiName": "weight",
                 				"fieldTypeApiName": "field_type_numeric",
                 				"fieldValue": "69"
                 			},
                 			{
                 				"fieldApiName": "postalCode",
                 				"fieldTypeApiName": "field_type_numeric",
                 				"fieldValue": "335300"
                 			},
                 			{
                 				"fieldApiName": "salaryBank",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "中国银行",
                 				"optionNameList": [
                 					"中国银行"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isVeteran",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isDisabled",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isCriminalRecord",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isDrugHistory",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isSeriousDisease",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isCompeteAgreement",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isIntellectualProperty",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isOtherInvestJob",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isCompanyBusiness",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isLiveDormitory",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isPoorHousehold",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isRelativeSameBusiness",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isNeedParking",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isNeedShuttle",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "否",
                 				"optionNameList": [
                 					"否"
                 				]
                 			},
                 			{
                 				"fieldApiName": "orderStatus",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "入职预约",
                 				"optionNameList": [
                 					"入职预约"
                 				]
                 			},
                 			{
                 				"fieldApiName": "cardRechargeActive",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "orderSignReview",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "线上合同推送",
                 				"optionNameList": [
                 					"线上合同推送"
                 				]
                 			},
                 			{
                 				"fieldApiName": "isSubmitPhoto",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "是",
                 				"optionNameList": [
                 					"是"
                 				]
                 			},
                 			{
                 				"fieldApiName": "sendDeptReportNotice",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "cardHandoverFinish",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "entrySatisfactionEval",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "eventContent",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "入职"
                 			},
                 			{
                 				"fieldApiName": "positionApplyForm",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-职位申请表.xlsx",
                 							"docAddress": "/backend/storage/resource?req=36801197",
                 							"size": 50286
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "interviewEvalForm",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-面试评估表.xlsx",
                 							"docAddress": "/backend/storage/resource?req=36801202",
                 							"size": 50286
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "backgroundSurveyForm",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-背景调查表.xlsx",
                 							"docAddress": "/backend/storage/resource?req=36801211",
                 							"size": 50286
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "evaluationReport",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-测评报告.docx",
                 							"docAddress": "/backend/storage/resource?req=36801216",
                 							"size": 0
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "personalResume",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-个人简历.docx",
                 							"docAddress": "/backend/storage/resource?req=36801266",
                 							"size": 0
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "physicalExamReport",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-体检报告.docx",
                 							"docAddress": "/backend/storage/resource?req=36801236",
                 							"size": 0
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "staffPhotobak",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-员工照片.jpeg",
                 							"docAddress": "/backend/storage/resource?req=36801272",
                 							"size": 24321
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "dataUploadCommitmentBAK",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-资料上传真实性承诺.docx",
                 							"docAddress": "/backend/storage/resource?req=36801278",
                 							"size": 0
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "validIdCard",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-个人有效身份证.jpg",
                 							"docAddress": "/backend/storage/resource?req=36801295",
                 							"size": 3292702
                 						},
                 						{
                 							"name": "OU*� 2025-06-07 174553.png",
                 							"docAddress": "/backend/storage/resource?req=37413653",
                 							"size": 1352
                 						},
                 						{
                 							"name": "屏幕截图 2025-07-13 094959.png",
                 							"docAddress": "/backend/storage/resource?req=37414162",
                 							"size": 6200
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "diplomaRecordForm",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-毕业证电子学历注册备案表.xlsx",
                 							"docAddress": "/backend/storage/resource?req=36801301",
                 							"size": 50286
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "degreeCertificate",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-学位证.pdf",
                 							"docAddress": "/backend/storage/resource?req=36801392",
                 							"size": 1345
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "dimissionCert",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-离职证明解除劳动合同通知书.docx",
                 							"docAddress": "/backend/storage/resource?req=36801337",
                 							"size": 0
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "salaryBankCard",
                 				"fieldTypeApiName": "field_type_attachment",
                 				"attachment": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-工资银行卡.jpg",
                 							"docAddress": "/backend/storage/resource?req=36801471",
                 							"size": 315195
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "idCardNo",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "440106199503201234"
                 			},
                 			{
                 				"fieldApiName": "zuzhibumen",
                 				"fieldTypeApiName": "field_type_lookup",
                 				"fieldValue": "162472106",
                 				"foreignDataName": "DFXK公司办公室"
                 			},
                 			{
                 				"fieldApiName": "gangwei",
                 				"fieldTypeApiName": "field_type_lookup",
                 				"fieldValue": "162473005",
                 				"foreignDataName": "COO（首席运营官）"
                 			},
                 			{
                 				"fieldApiName": "bankAccount",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "6217683905271866358"
                 			},
                 			{
                 				"fieldApiName": "ruzhijingbanren",
                 				"fieldTypeApiName": "field_type_staff",
                 				"fieldValue": "79904",
                 				"userEmail": "sls03@udesk.cn"
                 			},
                 			{
                 				"fieldApiName": "contractHandler",
                 				"fieldTypeApiName": "field_type_staff",
                 				"fieldValue": "79908",
                 				"userEmail": "sls07@udesk.cn"
                 			},
                 			{
                 				"fieldApiName": "privacyNoticeConfirmTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-15 20:32:00"
                 			},
                 			{
                 				"fieldApiName": "privacyNoticeConfirm",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "onboardGuideReadTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-15 20:32:00"
                 			},
                 			{
                 				"fieldApiName": "onboardGuideRead",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "truthCommitmentConfirm",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "truthCommitmentTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-15 20:43:00"
                 			},
                 			{
                 				"fieldApiName": "empNoCreate",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "empMasterDataSync",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "visitorReservationSend",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "employeeCardPrepare",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "onboardCheckIn",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "完成",
                 				"optionNameList": [
                 					"完成"
                 				]
                 			},
                 			{
                 				"fieldApiName": "onboardCheckInTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-18 17:39:00"
                 			},
                 			{
                 				"fieldApiName": "idCardAiAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "idCardManualAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "degreeAiAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "degreeManualAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "nameAiAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "nameManualAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "genderAiAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "genderManualAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "idNoAiAuditResultbak",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "通过",
                 				"optionNameList": [
                 					"通过"
                 				]
                 			},
                 			{
                 				"fieldApiName": "entryWay",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "手工",
                 				"optionNameList": [
                 					"手工"
                 				]
                 			},
                 			{
                 				"fieldApiName": "certNoAiAuditResult",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "是",
                 				"optionNameList": [
                 					"是"
                 				]
                 			},
                 			{
                 				"fieldApiName": "certificateType",
                 				"fieldTypeApiName": "field_type_single_listbox",
                 				"fieldValue": "居民身份证",
                 				"optionNameList": [
                 					"居民身份证"
                 				]
                 			},
                 			{
                 				"fieldApiName": "entryServiceScore",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "10分"
                 			},
                 			{
                 				"fieldApiName": "entryProcessScore",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "10分"
                 			},
                 			{
                 				"fieldApiName": "entryEvaluateContent",
                 				"fieldTypeApiName": "field_type_multi_line",
                 				"fieldValue": "入职流程顺畅高效，服务贴心周到，整体体验非常满意。\\n办理入职速度快，流程简单清晰，工作人员态度很好。\\n全程办理顺利，指引到位，对本次入职服务十分满意。"
                 			},
                 			{
                 				"fieldApiName": "zhengjianhao",
                 				"fieldTypeApiName": "field_type_single_line",
                 				"fieldValue": "440106199503201234"
                 			},
                 			{
                 				"fieldApiName": "zhengjiandaoqiriqi",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2026-05-20"
                 			},
                 			{
                 				"fieldApiName": "staffPhoto",
                 				"fieldTypeApiName": "field_type_image",
                 				"picture": {
                 					"attachmentList": [
                 						{
                 							"name": "杨俊平-员工照片.jpeg",
                 							"docAddress": "/backend/storage/resource?req=37213218",
                 							"size": 24321
                 						}
                 					]
                 				}
                 			},
                 			{
                 				"fieldApiName": "joinDate",
                 				"fieldTypeApiName": "field_type_date",
                 				"fieldValue": "2026-05-25"
                 			},
                 			{
                 				"fieldApiName": "personalPhone",
                 				"fieldTypeApiName": "field_type_telephone",
                 				"tagValueList": [
                 					{
                 						"tagName": "工作",
                 						"tagValue": "15651818750"
                 					}
                 				]
                 			},
                 			{
                 				"fieldApiName": "createTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-12 15:33:35"
                 			},
                 			{
                 				"fieldApiName": "updateTime",
                 				"fieldTypeApiName": "field_type_date_time",
                 				"fieldValue": "2026-05-25 20:01:57"
                 			},
                 			{
                 				"fieldApiName": "createUser",
                 				"fieldTypeApiName": "field_type_staff",
                 				"fieldValue": "63095",
                 				"userEmail": "15651818750@udesk.cn"
                 			},
                 			{
                 				"fieldApiName": "updateUser",
                 				"fieldTypeApiName": "field_type_staff",
                 				"fieldValue": "63095",
                 				"userEmail": "15651818750@udesk.cn"
                 			},
                 			{
                 				"fieldApiName": "owner",
                 				"fieldTypeApiName": "field_type_owner",
                 				"fieldValue": "63095",
                 				"ownerResult": {
                 					"ownerType": 1,
                 					"ownerName": "揭军平"
                 				}
                 			}
                 		]
                """;

        JsonNode resp = serviceGoClient.updateData("entryWorkOrder", DATA_ID, fieldDataListJson);
        System.out.println("=== 更新入职工单 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
