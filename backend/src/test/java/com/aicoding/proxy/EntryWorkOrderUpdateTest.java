package com.aicoding.proxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

public class EntryWorkOrderUpdateTest {

    private static final String HOST = "https://servicego.udesk.cn";
    private static final String EMAIL = "ServiceGo.demo@udesk.cn";
    private static final String API_TOKEN = "51c8de11ba94e7a056b832414fdc8b15";
    private static final String OBJECT_API_NAME = "entryWorkOrder";

    public static void main(String[] args) throws Exception {
        updateForeignName(162352692L, "yangjunping-Gary");
    }

    /**
     * 按 id 更新入职工单的 foreignName 字段
     * 接口规范 3.2 PUT /api/v1/data
     */
    public static void updateForeignName(long id, String foreignName) throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        String signInput = EMAIL + "&" + API_TOKEN + "&" + timestamp;
        String sign = sha256(signInput);

        String urlString = HOST + "/api/v1/data?"
                + "email=" + URLEncoder.encode(EMAIL, StandardCharsets.UTF_8)
                + "&timestamp=" + timestamp
                + "&sign=" + sign;

        String body = "{"
                + "\"objectApiName\":\"" + OBJECT_API_NAME + "\","
                + "\"id\":" + id + ","
                + "\"fieldDataList\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"name\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_auto_number\",\n" +
                "\t\t\t\t\"fieldValue\": \"20260512-0001\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"recruitChannel\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"劳务外包\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"劳务外包\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"recruitChannelSub\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"重庆拓高人力资源管理有限公司\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"重庆拓高人力资源管理有限公司\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"referrerStaff\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_staff\",\n" +
                "\t\t\t\t\"fieldValue\": \"39657\",\n" +
                "\t\t\t\t\"userEmail\": \"linchangbin@qq.com\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"eventReason\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"劳务工转入\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"劳务工转入\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"controlOrg\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"控股职能部门\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"控股职能部门\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"employTypeCategory\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"外包工\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"外包工\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"employType\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"外包工\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"外包工\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"deptOrgDesc\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"庆小康控股有限公司/重庆小康工业集团股份有限公司/东风小康汽车有限公司/DFXK公司办公室\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"postbak\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"入职办理完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"入职办理完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entryDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-13\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"employer\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"重庆小康工业集团股份有限公司\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"重庆小康工业集团股份有限公司\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"workAddress\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"重庆两江新区\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"重庆两江新区\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"dispatchCompany\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"重庆云雷人力资源管理服务有限公司\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"重庆云雷人力资源管理服务有限公司\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"directIndirectType\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"直接生产员工\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"直接生产员工\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"jobRank\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"4级\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"4级\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"jobLevel\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"3\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"3\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"rankLevelMerge\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"4级-3\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"outerWorkAgeMonth\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_numeric\",\n" +
                "\t\t\t\t\"fieldValue\": \"48\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"workUnit\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"重庆小康工业集团股份有限公司\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"重庆小康工业集团股份有限公司\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"staffStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"不在岗\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"不在岗\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"probationMonth\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_numeric\",\n" +
                "\t\t\t\t\"fieldValue\": \"36\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"probationEndDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-08-13\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"reportAddress\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"五云湖基地\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"五云湖基地\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"staffNo\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026001001\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"chineseName\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"杨俊平\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"foreignName\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"yangjunping\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"workStartDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2020-07-05\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"personalPhonebak\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_telephone\",\n" +
                "\t\t\t\t\"tagValueList\": [\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"tagName\": \"工作\",\n" +
                "\t\t\t\t\t\t\"tagValue\": \"15651818750\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"personalEmail\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_email\",\n" +
                "\t\t\t\t\"tagValueList\": [\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"tagName\": \"工作\",\n" +
                "\t\t\t\t\t\t\"tagValue\": \"yangjunping@udesk.cn\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"nationality\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"中国大陆\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"中国大陆\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"gender\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"男\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"男\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"birthDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"1995-03-21\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"maritalStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"已婚\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"已婚\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"marryDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2024-05-16\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"nation\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"浙江省\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"浙江省\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"householdType\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"外市城镇\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"外市城镇\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"politicalStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"中共党员\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"中共党员\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"partyJoinDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2018-05-16\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"drivingLicenseType\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"B1照\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"B1照\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"healthStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"健康\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"健康\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"height\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_numeric\",\n" +
                "\t\t\t\t\"fieldValue\": \"175\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"weight\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_numeric\",\n" +
                "\t\t\t\t\"fieldValue\": \"69\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"postalCode\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_numeric\",\n" +
                "\t\t\t\t\"fieldValue\": \"335300\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"salaryBank\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"中国银行\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"中国银行\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isVeteran\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isDisabled\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isCriminalRecord\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isDrugHistory\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isSeriousDisease\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isCompeteAgreement\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isIntellectualProperty\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isOtherInvestJob\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isCompanyBusiness\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isLiveDormitory\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isPoorHousehold\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isRelativeSameBusiness\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isNeedParking\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isNeedShuttle\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"否\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"否\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"orderStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"入职办理完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"入职办理完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"sendEntryEmail\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"sendEntrySms\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"cardRechargeActive\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"orderSignReview\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"线上合同推送\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"线上合同推送\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"isSubmitPhoto\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"是\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"是\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"sendDeptReportNotice\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"cardHandoverFinish\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entrySatisfactionEval\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"eventContent\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"入职\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"positionApplyForm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-职位申请表.xlsx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801197\",\n" +
                "\t\t\t\t\t\t\t\"size\": 50286\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"interviewEvalForm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-面试评估表.xlsx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801202\",\n" +
                "\t\t\t\t\t\t\t\"size\": 50286\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"backgroundSurveyForm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-背景调查表.xlsx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801211\",\n" +
                "\t\t\t\t\t\t\t\"size\": 50286\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"evaluationReport\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-测评报告.docx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801216\",\n" +
                "\t\t\t\t\t\t\t\"size\": 0\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"personalResume\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-个人简历.docx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801266\",\n" +
                "\t\t\t\t\t\t\t\"size\": 0\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"physicalExamReport\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-体检报告.docx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801236\",\n" +
                "\t\t\t\t\t\t\t\"size\": 0\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"staffPhotobak\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-员工照片.jpeg\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801272\",\n" +
                "\t\t\t\t\t\t\t\"size\": 24321\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"dataUploadCommitmentBAK\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-资料上传真实性承诺.docx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801278\",\n" +
                "\t\t\t\t\t\t\t\"size\": 0\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"validIdCard\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-个人有效身份证.jpg\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801295\",\n" +
                "\t\t\t\t\t\t\t\"size\": 3292702\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"diplomaRecordForm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-毕业证电子学历注册备案表.xlsx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801301\",\n" +
                "\t\t\t\t\t\t\t\"size\": 50286\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"degreeCertificate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-学位证.pdf\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801392\",\n" +
                "\t\t\t\t\t\t\t\"size\": 1345\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"dimissionCert\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-离职证明解除劳动合同通知书.docx\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801337\",\n" +
                "\t\t\t\t\t\t\t\"size\": 0\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"salaryBankCard\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_attachment\",\n" +
                "\t\t\t\t\"attachment\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-工资银行卡.jpg\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=36801471\",\n" +
                "\t\t\t\t\t\t\t\"size\": 315195\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"idCardNo\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"440106199503201234\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"zuzhibumen\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_lookup\",\n" +
                "\t\t\t\t\"fieldValue\": \"162472106\",\n" +
                "\t\t\t\t\"foreignDataName\": \"DFXK公司办公室\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"gangwei\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_lookup\",\n" +
                "\t\t\t\t\"fieldValue\": \"162473005\",\n" +
                "\t\t\t\t\"foreignDataName\": \"COO（首席运营官）\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"bankAccount\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"6217683905271866358\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"ruzhijingbanren\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_staff\",\n" +
                "\t\t\t\t\"fieldValue\": \"79904\",\n" +
                "\t\t\t\t\"userEmail\": \"sls03@udesk.cn\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"contractHandler\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_staff\",\n" +
                "\t\t\t\t\"fieldValue\": \"79908\",\n" +
                "\t\t\t\t\"userEmail\": \"sls07@udesk.cn\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"emailSendStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"发送成功\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"发送成功\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"smsSendStatus\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"发送成功\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"发送成功\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"privacyNoticeConfirmTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-15 20:32:00\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"privacyNoticeConfirm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"onboardGuideReadTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-15 20:32:00\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"onboardGuideRead\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"truthCommitmentConfirm\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"truthCommitmentTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-15 20:43:00\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"empNoCreate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"empMasterDataSync\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"visitorReservationSend\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"employeeCardPrepare\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"onboardCheckIn\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"完成\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"完成\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"onboardCheckInTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-18 17:39:00\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"idCardAiAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"idCardManualAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"degreeAiAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"degreeManualAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"nameAiAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"nameManualAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"genderAiAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"genderManualAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"idNoAiAuditResultbak\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"通过\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"通过\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entryWay\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"手工\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"手工\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"certNoAiAuditResult\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"是\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"是\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"certificateType\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_listbox\",\n" +
                "\t\t\t\t\"fieldValue\": \"居民身份证\",\n" +
                "\t\t\t\t\"optionNameList\": [\n" +
                "\t\t\t\t\t\"居民身份证\"\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entryServiceScore\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"10分\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entryProcessScore\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"10分\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"entryEvaluateContent\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_multi_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"入职流程顺畅高效，服务贴心周到，整体体验非常满意。\\n办理入职速度快，流程简单清晰，工作人员态度很好。\\n全程办理顺利，指引到位，对本次入职服务十分满意。\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"zhengjianhao\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_single_line\",\n" +
                "\t\t\t\t\"fieldValue\": \"440106199503201234\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"zhengjiandaoqiriqi\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-20\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"staffPhoto\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_image\",\n" +
                "\t\t\t\t\"picture\": {\n" +
                "\t\t\t\t\t\"attachmentList\": [\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\"name\": \"杨俊平-员工照片.jpeg\",\n" +
                "\t\t\t\t\t\t\t\"docAddress\": \"/backend/storage/resource?req=37213218\",\n" +
                "\t\t\t\t\t\t\t\"size\": 24321\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"joinDate\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-25\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"personalPhone\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_telephone\",\n" +
                "\t\t\t\t\"tagValueList\": [\n" +
                "\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\"tagName\": \"工作\",\n" +
                "\t\t\t\t\t\t\"tagValue\": \"15651818750\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"createTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-12 15:33:35\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"updateTime\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_date_time\",\n" +
                "\t\t\t\t\"fieldValue\": \"2026-05-25 17:15:07\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"createUser\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_staff\",\n" +
                "\t\t\t\t\"fieldValue\": \"63095\",\n" +
                "\t\t\t\t\"userEmail\": \"15651818750@udesk.cn\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"updateUser\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_staff\",\n" +
                "\t\t\t\t\"fieldValue\": \"63095\",\n" +
                "\t\t\t\t\"userEmail\": \"15651818750@udesk.cn\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"fieldApiName\": \"owner\",\n" +
                "\t\t\t\t\"fieldTypeApiName\": \"field_type_owner\",\n" +
                "\t\t\t\t\"fieldValue\": \"63095\",\n" +
                "\t\t\t\t\"ownerResult\": {\n" +
                "\t\t\t\t\t\"ownerType\": 1,\n" +
                "\t\t\t\t\t\"ownerName\": \"揭军平\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t]"
                + "}";

        System.out.println("=== 更新入职工单 foreignName ===");
        System.out.println("URL: " + urlString);
        System.out.println("Sign input: " + signInput);
        System.out.println("Sign: " + sign);
        System.out.println("Body: " + body);
        System.out.println();

        String response = sendPut(urlString, body);
        System.out.println("Response: " + response);
    }

    public static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA256计算失败", e);
        }
    }

    public static String sendPut(String urlString, String body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP Status: " + responseCode);

        StringBuilder response = new StringBuilder();
        try (var reader = new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                StandardCharsets.UTF_8)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                response.append((char) ch);
            }
        }

        return response.toString();
    }
}
