package com.wonders.sjtb.at;

import com.wonders.Constants;
import com.wonders.log.entity.OperateLog;
import com.wonders.query.entity.CorpInfo;
import com.wonders.qxkz.QXConstants;
import com.wonders.sjtb.entity.*;
import com.wonders.sjtb.service.ParseService;
import com.wonders.tiles.authority.entity.User;
import com.wonders.tiles.authority.service.SystemConstants;
import com.wonders.tiles.dic.DicDataUtils;
import com.wonders.tiles.extend.adaptor.util.ConUtils;
import com.wonders.util.PropertyUtils;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ViewWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.Boolean;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@At("/sjtb")
@IocBean
public class SjtbAt {
    @Inject
    private Dao dao;
    @Inject
    private ParseService parseService;

    @At
    @Ok("jsp:jsp.sjtb.tbymbackgroud")
    public Map<String, Object> toTbymBackgroud(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Pager pager = ConUtils.makePaginationPager(request);
        Criteria cri = Cnd.cri();
        cri.where().and("VALID", "=", "1");
        cri.getOrderBy().desc("TB_DATE");
        List<TbFile> tblist = dao.query(TbFile.class, cri, pager);
        int rsListCount = dao.count(TbFile.class, cri);
        pager.setRecordCount(rsListCount);
        result.put("tblist", tblist);
        result.put("pager", pager);
        return result;
    }

    @At
    @Ok("jsp:jsp.sjtb.sjtb")
    public Map<String, Object> toSjtb(HttpSession session) {
        Map<String, Object> result = new HashMap<String, Object>();
        User user = (User) session.getAttribute(SystemConstants.SYSTEM_USER);
        Criteria cri = Cnd.cri();
        cri.where().and("DEPT_ID", "=", user.getDept());
        cri.getOrderBy().asc("ORDER_NO");
        List<TbContents> contents = dao.query(TbContents.class, cri);
        result.put("contents", contents);
        return result;
    }

    @At
    public void downloadMoban(String name, HttpServletResponse response) {
        try {
            TbContents contents = dao.fetch(TbContents.class,
                    Cnd.where("NAME", "=", name));
            File file = new File(getFilePath() + "/"
                    + PropertyUtils.getProperty("downloadmoban") + "/"
                    + contents.getTbFile());
            InputStream inStream = new FileInputStream(file);
            OutputStream outStream = response.getOutputStream();
            response.reset();
            String filename = contents.getTbFileName();
            response.addHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(filename, "utf-8"));
            int tempbyte;
            while ((tempbyte = inStream.read()) != -1) {
                outStream.write(tempbyte);
                outStream.flush();
            }
            outStream.close();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath() {
        String path = PropertyUtils.getProperty("app.path");
        return path;
    }

    private String getUploadPath() {
        String path = PropertyUtils.getProperty("app.path")
                + PropertyUtils.getProperty("file.tbupload.path");
        return path;
    }

    @At
    @Ok("jsp:jsp.sjtb.tbym")
    public Map<String, Object> toTbym(HttpServletRequest request, String type) {
        Pager pager = ConUtils.makePaginationPager(request);
        Map<String, Object> result = new HashMap<String, Object>();
        Criteria cri = Cnd.cri();
        cri.where().and("TB_TYPE", "=", type);
        cri.where().and("VALID", "=", "1");
        cri.getOrderBy().desc("TB_DATE");
        List<TbFile> tblist = dao.query(TbFile.class, cri, pager);
        int rsListCount = dao.count(TbFile.class,
                Cnd.where("TB_TYPE", "=", type).and("valid", "=", "1"));
        pager.setRecordCount(rsListCount);
        result.put("type", type);
        result.put("tblist", tblist);
        result.put("pager", pager);
        return result;
    }

    @At
    @Ok("jsp:jsp.wbj.index")
    public Map<String, Object> toIndex(String id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(SystemConstants.SYSTEM_USER);
        Map<String, Object> result = new HashMap<String, Object>();
        SAXReader reader = new SAXReader();
        String web_inf = session.getServletContext().getRealPath("/WEB-INF/");
        Document doc;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(web_inf + "/fw.xml"), "utf-8");
            doc = reader.read(isr);
            Document document = DocumentHelper.createDocument();
            Element root = DocumentHelper.createElement("dl");
            document.setRootElement(root);
//            数据填报
            if (user.getRoleId().contains(QXConstants.TB_USER)) {
                root.add((Node) doc.selectSingleNode("//dt[@id='menu1']").clone());
                root.add((Node) doc.selectSingleNode("//dd[@id='dd1']").clone());
            }
//            双公示填报
            if (user.getRoleId().contains(QXConstants.SGS_USER)) {
                root.add((Node) doc.selectSingleNode("//dt[@id='menu100']").clone());
                root.add((Node) doc.selectSingleNode("//dd[@id='dd100']").clone());
            }
            StringWriter writer = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter xmlwriter = new XMLWriter(writer, format);
            xmlwriter.write(root);
            result.put("menu", writer.toString());
        } catch (DocumentException | IOException e1) {
            e1.printStackTrace();
        }
        result.put("displayName", user.getDisplayName());
        result.put("id", id);
        return result;
    }


    @At
    @AdaptBy(type = UploadAdaptor.class, args = {"${app.root}/upload/"})
    @Ok("jsp:jsp.sjtb.tbym")
    public Map<String, Object> upload(TempFile tempFile, HttpServletRequest req,
                                      HttpSession session, String tbtype, String sjssyf)
            throws IOException {
        if (Strings.isEmpty(sjssyf)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            sjssyf = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
        }
        String datayear = sjssyf != null ? sjssyf.substring(0, 4) : null;
        String datamon = sjssyf != null ? datayear + sjssyf.substring(5, 7) : null;
        OperateLog log = new OperateLog();
        log.setOperateDate(new Date());
        log.setOperateType("上报文件");
        log.setOperateUser(((User) session.getAttribute(SystemConstants.SYSTEM_USER)).getLogonName());
        log.setOperateDept(((User) session.getAttribute(SystemConstants.SYSTEM_USER)).getDept());
        TbFile tbFile = upload(tempFile, session, tbtype, datayear, datamon);
        Map<String, Object> result = toTbym(req, tbtype);
        result.put("tbid", tbFile.getId());
        if (tbFile.getValid() == "0") {
            result.put("state", 0);
            log.setOperateResult("上报失败");
            dao.insert(log);
            return result;
        }
        parseService.parse(tbFile, tempFile, datamon, session);
        log.setOperateResult("上报成功");
        dao.insert(log);
        result.put("state", 1);
        return result;
    }

    @At
    @AdaptBy(type = UploadAdaptor.class, args = {"${app.root}/upload/"})
    @Ok("jsp:jsp.sjtb.tbymbackgroud")
    public Map<String, Object> uploadbackgroud(TempFile tempFile, HttpServletRequest req,
                                               HttpSession session, String tbtype, String sjssyf)
            throws IOException {
        if (Strings.isEmpty(sjssyf)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            sjssyf = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
        }
        String datayear = sjssyf != null ? sjssyf.substring(0, 4) : null;
        String datamon = sjssyf != null ? datayear + sjssyf.substring(5, 7) : null;
        OperateLog log = new OperateLog();
        log.setOperateDate(new Date());
        log.setOperateType("上报文件");
        log.setOperateUser(((User) session.getAttribute(SystemConstants.SYSTEM_USER)).getLogonName());
        log.setOperateDept(((User) session.getAttribute(SystemConstants.SYSTEM_USER)).getDept());
        TbFile tbFile = upload(tempFile, session, tbtype, datayear, datamon);
        Map<String, Object> result = toTbymBackgroud(req);
        result.put("type", tbtype);
        result.put("tbid", tbFile.getId());
        if (tbFile.getValid() == "0") {
            result.put("state", 0);
            log.setOperateResult("上报失败");
            dao.insert(log);
            return result;
        }
        parseService.parse(tbFile, tempFile, datamon, session);
        log.setOperateResult("上报成功");
        dao.insert(log);
        result.put("state", 1);
        return result;
    }

    private TbFile upload(TempFile tempFile, HttpSession session,
                          String tbtype, String datayear, String datamon) {
        TbFile tbFile = new TbFile();
        String fileId = UUID.randomUUID().toString().replaceAll("-", "");
        tbFile.setId(fileId);
        tbFile.setFilename(fileId + ".xls");
        tbFile.setTbdate(new Date());
        tbFile.setTbr(((User) session.getAttribute(SystemConstants.SYSTEM_USER))
                .getUserId());
        tbFile.setTbtype(tbtype);
        tbFile.setDataYear(datamon);
        Map<String, Object> fileCheck = parseService
                .fileCheck(tbtype, tempFile, session);
        if ((Boolean) fileCheck.get("checkresult") == false) {
            tbFile.setValid("0");
            tbFile.setErrorTitle((String) fileCheck.get("errortitle"));
            tbFile.setErrorContent((String) fileCheck.get("errorcontent"));
            dao.insert(tbFile);
            return tbFile;
        }
        tbFile.setValid("1");
        File file = tempFile.getFile();
        File dirFile = new File(getUploadPath());
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File resultFile = new File(getUploadPath() + "/" + tbFile.getFilename());
        try {
            FileUtils.copyFile(file, resultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao.insert(tbFile);
        return tbFile;
    }

    @At
    public View toFileYl(String fileid, String type) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("html", getYlHTML(fileid, type));
        result.put("type", type);
        return new ViewWrapper(new JspView("jsp.sjtb.yl.fileyl"), result);
    }

    @At
    public View viewHistory(String type, String id, String sheetid) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<TbColumns> columns = dao.query(
                TbColumns.class,
                Cnd.where("TABLE_BM", "=", type).and("SHEET_ID", "=",
                        Integer.parseInt(sheetid)));
        Collections.sort(columns);
        String html = createDataHTML(columns, id);
        result.put("html", html);
        return new ViewWrapper(new JspView("jsp.sjtb.view.historyview"), result);
    }

    private String createDataHTML(final List<TbColumns> columns, String id) {
        if (columns.size() < 1)
            return "";
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select ");
        for (int i = 0; i < columns.size(); i++) {
            sqlstr.append(columns.get(i).getColumnName());
            if (i < (columns.size() - 1)) {
                sqlstr.append(",");
            }
        }
        sqlstr.append(" from " + columns.get(0).getHistoryTableName()
                + " where id='" + id + "'");
        Sql sql = Sqls.create(sqlstr.toString());
        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection connection, ResultSet rs, Sql sql)
                    throws SQLException {
                StringBuffer htmlstr = new StringBuffer();
                htmlstr.append(
                        "<table class='table_multilist' width='96%' style='margin:auto'>")
                        .append("<tbody>");
                while (rs.next()) {
                    int flag = 0;
                    for (int i = 0; i < columns.size(); i++) {
                        if (flag % 2 == 0) {
                            htmlstr.append("<tr>");
                        }
                        htmlstr.append("<td class='label_1' align='center' width='20%'>"
                                + columns.get(i).getColumnComment() + ":</td>");
                        if (columns.get(i).getColumnType()
                                .equalsIgnoreCase("VARCHAR")) {
                            if (rs.getString(columns.get(i).getColumnName()) == null) {
                                htmlstr.append("<td class='label_2' align='center' width='30%'></td>");
                            } else {
                                htmlstr.append("<td class='label_2' align='center' width='30%'>"
                                        + rs.getString(columns.get(i)
                                        .getColumnName()) + "</td>");
                            }

                        }
                        if (columns.get(i).getColumnType()
                                .equalsIgnoreCase("NUMBER")) {
                            DecimalFormat df = new DecimalFormat("######0.00");
                            htmlstr.append("<td class='label_2' align='center' width='20%'>"
                                    + df.format(rs.getDouble(columns.get(i)
                                    .getColumnName())) + "</td>");
                        }
                        if (columns.get(i).getColumnType()
                                .equalsIgnoreCase("DATE")) {
                            if (rs.getDate(columns.get(i).getColumnName()) == null) {
                                htmlstr.append("<td class='label_2' align='center'  width='20%'></td>");
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        columns.get(i).getDateType());
                                htmlstr.append("<td class='label_2' align='center' width='20%'>"
                                        + sdf.format(rs.getDate(columns.get(i)
                                        .getColumnName())) + "</td>");
                            }
                        }
                        if (flag % 2 == 1) {
                            htmlstr.append("</tr>");
                        }
                        flag++;
                    }
                    break;
                }
                htmlstr.append("</tbody>");
                htmlstr.append("</table>");
                return htmlstr.toString();
            }
        });
        dao.execute(sql);
        return (String) sql.getResult();
    }

    /**
     * 预览页面 html
     *
     * @param fileid
     * @param type
     * @return
     */
    private String getYlHTML(String fileid, String type) {
        // 标签
        StringBuffer html = new StringBuffer();
        html.append("<div class='itab'><ul>");
        List<TbSheet> sheets = dao.query(TbSheet.class,
                Cnd.where("TABEL_BM", "=", type));
        Collections.sort(sheets);
        for (int i = 0; i < sheets.size(); i++) {
            if (i == 0) {
                html.append("<li><a href='#tab1' class='selected'>"
                        + sheets.get(i).getSheetName() + "</a></li>");
            } else {
                html.append("<li><a href='#tab" + (i + 1) + "' >"
                        + sheets.get(i).getSheetName() + "</a></li>");
            }
        }
        html.append("</ul></div>");
        for (int i = 0; i < sheets.size(); i++) {
            final TbSheet currentsheet = sheets.get(i);
            html.append("<div id='tab" + (i + 1) + "' class='tabson'>");
            html.append("<div align='center'>");
            html.append("<table width='96%' class='table_list'>");
            List<TbColumns> columns = dao.query(
                    TbColumns.class,
                    Cnd.where("TABLE_BM", "=", type)
                            .and("SHEET_ID", "=", sheets.get(i).getOrderNo())
                            .and("IS_YULAN", "=", "1"));
            Collections.sort(columns);
            final List<TbColumns> cols = columns;
            html.append("<tr>");
            html.append("<th>序号</th>");
            for (TbColumns tbColumns : columns) {
                html.append("<th>" + tbColumns.getColumnComment() + "</th>");
            }
            html.append("<th>操作</th>");
            html.append("</tr>");
            if (columns.size() > 0) {
                StringBuffer query = new StringBuffer("select ID,");
                for (int j = 0; j < columns.size(); j++) {
                    query.append(columns.get(j).getColumnName());
                    if (j < columns.size() - 1) {
                        query.append(",");
                    }
                }
                query.append(" from " + columns.get(0).getHistoryTableName());
                query.append(" where FILE_ID=@FILE_ID and SHEET_ID=@SHEET_ID");
                Sql sql = Sqls.create(query.toString());
                sql.setCallback(new SqlCallback() {

                    @Override
                    public Object invoke(Connection connection, ResultSet rs,
                                         Sql sql) throws SQLException {
                        StringBuffer html = new StringBuffer();
                        int index = 1;
                        while (rs.next()) {
                            html.append("<tr>");
                            html.append("<td align='center'>" + index + "</td>");
                            for (int i = 0; i < cols.size(); i++) {
                                if (cols.get(i).getColumnType()
                                        .equals("VARCHAR")) {
                                    if (rs.getString(cols.get(i)
                                            .getColumnName()) == null) {
                                        html.append("<td align='center'></td>");
                                    } else {
                                        String data = rs.getString(cols.get(i)
                                                .getColumnName());
                                        if (data.length() > 20) {
                                            data = data.substring(0, 20) + "...";
                                        }
                                        html.append("<td align='center'>"
                                                + data + "</td>");
                                    }

                                }
                                if (cols.get(i).getColumnType()
                                        .equals("NUMBER")) {
                                    DecimalFormat df = new DecimalFormat(
                                            "######0.00");
                                    html.append("<td align='center'>"
                                            + df.format(rs.getDouble(cols
                                            .get(i).getColumnName()))
                                            + "</td>");
                                }
                                if (cols.get(i).getColumnType().equals("DATE")) {
                                    if (rs.getDate(cols.get(i).getColumnName()) == null) {
                                        html.append("<td align='center'></td>");
                                    } else {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                                        html.append("<td align='center'>"
                                                + sdf.format(rs
                                                .getDate(cols
                                                        .get(i)
                                                        .getColumnName()))
                                                + "</td>");
                                    }

                                }
                            }
                            html.append("<td align='center'><a href='#' onclick=\"view('"
                                    + rs.getString("ID")
                                    + "',"
                                    + currentsheet.getOrderNo()
                                    + ")\">查看</a></td>");
                            html.append("</tr>");
                            index++;
                        }
                        return html.toString();
                    }
                });
                sql.params().set("FILE_ID", fileid);
                sql.params().set("SHEET_ID", sheets.get(i).getOrderNo());
                dao.execute(sql);
                html.append(sql.getResult());
            }
            html.append("</table>");
            html.append("</div>");
            html.append("</div>");
        }
        return html.toString();
    }

    @At
    @Ok("jsp:jsp.sjtb.fileerror")
    public Map<String, Object> toFileError(String fileid, String type) {
        Map<String, Object> result = new HashMap<String, Object>();
        TbFile file = dao.fetch(TbFile.class, Cnd.where("id", "=", fileid));
        result.put("file", file);
        dao.delete(TbFile.class, fileid);
        return result;
    }

    /**
     * 数据查询
     *
     * @return
     */
    @At
    @Ok("jsp:jsp.sjtb.data.data")
    public Map<String, Object> toSeeData(HttpServletRequest request, String sjssyf, String dept, String sjlx, String sheet) {
        Pager pager = ConUtils.makePaginationPager(request);
        Map<String, Object> result = new HashMap<String, Object>();
        String month = "";
        if (!Strings.isEmpty(sjssyf)) {
            month = sjssyf.substring(0, 4) + sjssyf.substring(5, 7);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date sjssyfdDate = format.parse(sjssyf + "-01");
                result.put("sjssyf", sjssyfdDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Strings.isEmpty(sjlx) && !Strings.isEmpty(sheet)) {
            Criteria cri = Cnd.cri();
            cri.where().and("TABLE_BM", "=", sjlx);
            cri.where().and("IS_YULAN", "=", "1");
            cri.where().and("SHEET_ID", "=", sheet);
            cri.getOrderBy().asc("ORDER_NO");
            List<TbColumns> columns = dao.query(TbColumns.class, cri);
            int i = 0;
            for (; i < columns.size(); i++) {
                if (columns.get(i).getIsWybs() == null || !columns.get(i).getIsWybs().equals("1"))
                    break;
            }
            result.put("html", createDataYulanHTML(columns, month, pager, i, sheet));
            String countstr = "select count(1) counts from " + columns.get(0).getTableName() + " where  " + columns.get(i).getColumnName() + " is not null";
            if (!Strings.isEmpty(month)) {
                countstr = countstr + " and DATA_YEAR='" + month + "'";
            }
            Sql countsSql = Sqls.create(countstr);
            countsSql.setCallback(new SqlCallback() {

                @Override
                public Object invoke(Connection arg0, ResultSet rs, Sql arg2)
                        throws SQLException {
                    while (rs.next()) {
                        return rs.getInt("counts");
                    }
                    return 0;
                }
            });
            dao.execute(countsSql);
            pager.setRecordCount((Integer) countsSql.getResult());
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SystemConstants.SYSTEM_USER);
        if (user.getRoleId().indexOf(QXConstants.ADMIN) >= 0) {
            result.put("admin", true);
        }
        result.put("sheet", sheet);
        result.put("pager", pager);
        result.put("dept", dept);
        result.put("sjlx", sjlx);
        result.put("month", month);
        return result;
    }

    @At
    public void exportData(String sjssyf, String dept, String sjlx, String sheet, HttpServletResponse response, HttpServletRequest request) throws WriteException, IOException {
        String month = "";
        Pager pager = dao.createPager(1, 2000);
        if (!Strings.isEmpty(sjlx) && !Strings.isEmpty(sheet)) {
            Criteria cri = Cnd.cri();
            cri.where().and("TABLE_BM", "=", sjlx);
            cri.where().and("SHEET_ID", "=", sheet);
            cri.getOrderBy().asc("ORDER_NO");
            List<TbColumns> columns = dao.query(TbColumns.class, cri);
            TbSheet tbsheet = dao.fetch(TbSheet.class, Cnd.where("TABEL_BM", "=", sjlx).and("ORDER_NO", "=", sheet));
            StringBuffer sqlcon = new StringBuffer();
            sqlcon.append("select ");
            int k = 0;
            for (k = 0; k < columns.size(); k++) {
                sqlcon.append(columns.get(k).getColumnName()).append(",");
            }
            sqlcon.append("DATA_YEAR").append(" from " + columns.get(k - 1).getTableName() + " where ").append(columns.get(k - 1).getColumnName()).append(" is not null");
            if (!Strings.isEmpty(sjssyf)) {
                month = sjssyf.substring(0, 4) + sjssyf.substring(5, 7);
                sqlcon.append(" and DATA_YEAR='" + month + "'");
            }
            Sql sql = Sqls.create(sqlcon.toString());
            sql.setPager(pager);
            sql.setCallback(new SqlCallback() {

                @Override
                public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int count = rsmd.getColumnCount();
                    String[] name = new String[count];
                    for (int i = 0; i < count; i++) {
                        name[i] = rsmd.getColumnName(i + 1);
                    }
                    while (rs.next()) {
                        Map<String, Object> map1 = new HashMap<String, Object>();
                        for (String key : name) {
                            map1.put(key.toLowerCase(), rs.getString(key));
                        }
                        list.add(map1);
                    }
                    return list;
                }
            });
            dao.execute(sql);
            List<Map<String, Object>> list = (List<Map<String, Object>>) sql.getResult();
            WritableWorkbook workbook = null;
            try {
                OutputStream os = response.getOutputStream();
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(tbsheet.getSheetName() + ".xls", "utf-8"));
                response.setContentType("application/msexcel");
                workbook = Workbook.createWorkbook(os);
                WritableSheet sheet1 = workbook.createSheet(tbsheet.getSheetName(), 0);
                jxl.SheetSettings sheetset = sheet1.getSettings();
                sheetset.setProtected(false);
                /** ************设置单元格字体************** */
                WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 10);
                WritableFont BoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                /** ************以下设置三种单元格样式，灵活备用************ */
                // 用于标题居中
                WritableCellFormat wcf_center = new WritableCellFormat(BoldFont);
                wcf_center.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
                wcf_center.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
                wcf_center.setAlignment(Alignment.CENTRE); // 文字水平对齐
                wcf_center.setWrap(false); // 文字是否换行
                // 用于正文居左
                WritableCellFormat wcf_left = new WritableCellFormat(NormalFont);
                wcf_left.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
                wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
                wcf_left.setAlignment(Alignment.LEFT); // 文字水平对齐
                wcf_left.setWrap(false); // 文字是否换行
                /** ***************以下是EXCEL第一行列标题********************* */
                sheet1.addCell(new Label(0, 0, "序号", wcf_center));
                int i = 0;
                for (i = 0; i < columns.size(); i++) {
                    sheet1.addCell(new Label(i + 1, 0, columns.get(i).getColumnComment(), wcf_center));
                }
                sheet1.addCell(new Label(i + 1, 0, (String) ("数据所属月份"), wcf_center));
                /** ***************以下是EXCEL正文数据********************* */
                int d = 1;
                int j = 0;
                for (Map<String, Object> info : list) {
                    for (j = 0; j < columns.size(); j++) {
                        sheet1.addCell(new Label(j + 1, d, (String) (info.get(columns.get(j).getColumnName()) == null ? "" : info.get(columns.get(j).getColumnName())), wcf_left));
                    }
                    sheet1.addCell(new Label(0, d, String.valueOf(d), wcf_left));
                    sheet1.addCell(new Label(j + 1, d, (String) (info.get("data_year")), wcf_left));
                    d++;
                }
                /** **********将以上缓存中的内容写到EXCEL文件中******** */
                workbook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workbook.close();
            }
        }
    }

    @At
    @Ok("json")
    public Object getSjlx(String dept) {
        NutMap re = new NutMap();
        List<TbContents> contents = dao.query(TbContents.class, Cnd.where("DEPT_ID", "=", dept));
        re.put("contents", contents);
        return re;
    }

    @At
    @Ok("json")
    public Object getSheet(String sjlx) {
        NutMap re = new NutMap();
        Criteria cri = Cnd.cri();
        cri.where().and("TABEL_BM", "=", sjlx);
        cri.getOrderBy().asc("ORDER_NO");
        List<TbSheet> sheets = dao.query(TbSheet.class, cri);
        re.put("sheets", sheets);
        return re;
    }

    private String createDataYulanHTML(final List<TbColumns> columns, String month, Pager pager, int index, final String sheet) {
        StringBuffer sqlstr = new StringBuffer();
        StringBuffer html = new StringBuffer();
        html.append("<table width='96%' class='table_list'>");
        html.append("<tr>");
        html.append("<th>序号</th>");
        sqlstr.append("select ID,");
        for (int i = 0; i < columns.size(); i++) {
            sqlstr.append(columns.get(i).getColumnName());
            if (i < columns.size() - 1) {
                sqlstr.append(",");
            }
            html.append("<th>" + columns.get(i).getColumnComment() + "</th>");
        }
        sqlstr.append(",DATA_YEAR ");
        html.append("<th>数据所属月份</th>");
        html.append("<th>操作</th>");
        html.append("</tr>");
        sqlstr.append(" from " + columns.get(0).getTableName());
        sqlstr.append(" where 1= 1 ");
        if (!Strings.isEmpty(month)) {
            sqlstr.append(" and DATA_YEAR='" + month + "'");
        }
        sqlstr.append(" and " + columns.get(index).getColumnName() + " is not null order by DATA_YEAR desc");
        Sql sql = Sqls.create(sqlstr.toString());
        sql.setPager(pager);
        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection connection, ResultSet rs, Sql sql)
                    throws SQLException {
                StringBuffer str = new StringBuffer();
                int j = 1;
                while (rs.next()) {
                    str.append("<tr>");
                    str.append("<td align='center'>" + j + "</td>");
                    for (int i = 0; i < columns.size(); i++) {
                        if (columns.get(i).getColumnType().equals("DATE")) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                            str.append("<td align='center'>" + format.format(rs.getDate(columns.get(i).getColumnName())) + "</td>");
                        } else {
                            String data = rs.getString(columns.get(i).getColumnName());
                            if (data == null)
                                data = "";
                            if (data.length() > 20) {
                                data = data.substring(0, 20) + "...";
                            }
                            str.append("<td align='center'>" + data + "</td>");
                        }
                    }
                    String month = rs.getString("DATA_YEAR");
                    if (!Strings.isEmpty(month)) {
                        if (month.length() == 4) {
                            str.append("<td align='center'>" + month + "年</td>");
                        }
                        if (month.length() == 6) {
                            str.append("<td align='center'>" + month.substring(0, 4) + "年" + month.substring(4, 6) + "月</td>");
                        }
                    } else {
                        str.append("<td align='center'></td>");
                    }

                    str.append("<td align='center'><a href='#' "
                            + "onclick=\"view('" + rs.getString("ID") + "','" + columns.get(0).getTableBm() + "','" + sheet + "')\">"
                            + "查看</a></td>");
                    str.append("</tr>");
                    j++;
                }
                return str.toString();
            }
        });
        dao.execute(sql);
        html.append(sql.getResult());
        html.append("</table>");
        return html.toString();
    }

    @At
    @Ok("jsp:jsp.sjtb.data.view")
    public Map<String, Object> view(String id, String sjlx, String sheet_id) {
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuffer html = new StringBuffer();
        Criteria criteria = Cnd.cri();
        criteria.where().and("TABEL_BM", "=", sjlx);
        criteria.where().and("ORDER_NO", "=", sheet_id);
        criteria.getOrderBy().asc("ORDER_NO");
        List<TbSheet> sheets = dao.query(TbSheet.class, criteria);
        for (TbSheet sheet : sheets) {
            Criteria cri = Cnd.cri();
            cri.where().and("TABLE_BM", "=", sjlx);
            cri.where().and("SHEET_ID", "=", sheet.getOrderNo());
            cri.getOrderBy().asc("ORDER_NO");
            final List<TbColumns> columns = dao.query(TbColumns.class, cri);
            StringBuffer sqlstr = new StringBuffer();
            sqlstr.append("select ");
            for (int i = 0; i < columns.size(); i++) {
                sqlstr.append(columns.get(i).getColumnName());
                if (i < columns.size() - 1) {
                    sqlstr.append(",");
                }
            }
            sqlstr.append(" from " + columns.get(0).getTableName() + " where ID='" + id + "'");
            Sql sql = Sqls.create(sqlstr.toString());
            sql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection connection, ResultSet rs, Sql sql)
                        throws SQLException {
                    while (rs.next()) {
                        StringBuffer html = new StringBuffer();
                        html.append("<table class='table_multilist' width='96%' style='margin:auto'>").append("<tbody>");
                        int sum = 0;
                        for (int i = 0; i < columns.size(); i++) {
                            if (sum % 2 == 0) {
                                html.append("<tr>");
                            }
                            html.append("<td class='label_1' align='center' width='20%'>" + columns.get(i).getColumnComment() + ":<td>");
                            if (rs.getObject(columns.get(i).getColumnName()) == null) {
                                html.append("<td class='label_2' align='center' width='30%'><td>");
                            } else {
                                if (columns.get(i).getColumnType().equals("DATE")) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                                    html.append("<td class='label_2' align='center' width='30%'>" + format.format(rs.getDate(columns.get(i).getColumnName())) + "<td>");
                                } else {
                                    html.append("<td class='label_2' align='center' width='30%'>" + rs.getString(columns.get(i).getColumnName()) + "<td>");
                                }
                            }
                            if (sum % 2 != 0) {
                                html.append("</tr>");
                            }
                            sum++;
                        }
                        html.append("</tbody>").append("</table>");
                        return html.toString();
                    }
                    return null;
                }
            });
            dao.execute(sql);
            html.append("<div><p style='text-align:center;font-size:20px;padding-bottom: 15px;padding-top: 10px'><b>" + sheet.getSheetName() + "</b></p></div>");
            html.append(sql.getResult());
        }
        result.put("html", html.toString());
        return result;
    }

    @At
    @Ok("jsp:jsp.sjtb.count")
    public Map<String, Object> toTbCount(String datamonth, String datamonth1, HttpSession session) {
        Map<String, Object> result = new HashMap<String, Object>();
        User user = (User) session.getAttribute(Constants.USER);
        String roles = user.getRoleId();
        if (Strings.isEmpty(datamonth) && Strings.isEmpty(datamonth1)) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1);
            result.put("datamonth", date);
            result.put("datamonth1", date);
            datamonth = new SimpleDateFormat("yyyyMM").format(calendar.getTime());
            datamonth1 = new SimpleDateFormat("yyyyMM").format(calendar.getTime());
            result.put("ssyf", datamonth);
            result.put("ssyf1", datamonth1);
        } else {
            try {
                result.put("datamonth", new SimpleDateFormat("yyyy-MM-dd").parse(datamonth + "-01"));
                result.put("datamonth1", new SimpleDateFormat("yyyy-MM-dd").parse(datamonth1 + "-01"));
                Calendar calendar = Calendar.getInstance();
                Calendar calendar1 = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(datamonth + "-01"));
                calendar1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(datamonth1 + "-01"));
                calendar.add(Calendar.MONTH, -1);
                calendar1.add(Calendar.MONTH, -1);
                datamonth = new SimpleDateFormat("yyyyMM").format(calendar.getTime());
                datamonth1 = new SimpleDateFormat("yyyyMM").format(calendar1.getTime());
                result.put("ssyf", datamonth);
                result.put("ssyf1", datamonth1);
            } catch (ParseException e) {
            }
        }

        List<TbContents> tbcontents = dao.query(TbContents.class, null);
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;

        for (TbContents t : tbcontents) {
            if (t.getOrderNo() == 1) {
                Sql sql = Sqls.create("select count(1) count from " + t.getTableName() + " where DATA_YEAR between to_number(" + datamonth.replaceAll("-", "") + ") and to_number(" + datamonth1.replaceAll("-", "") + ")");
                sql.setCallback(new SqlCallback() {

                    @Override
                    public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                        // TODO Auto-generated method stub

                        while (rs.next()) {
                            return rs.getInt("count");
                        }
                        return 0;
                    }
                });
                dao.execute(sql);
                int a1 = (Integer) sql.getResult();
                count1 = count1 + a1;
            } else if (t.getOrderNo() == 2) {
                Sql sql = Sqls.create("select count(1) count from " + t.getTableName() + " where DATA_YEAR between to_number(" + datamonth.replaceAll("-", "") + ") and to_number(" + datamonth1.replaceAll("-", "") + ")");
                sql.setCallback(new SqlCallback() {

                    @Override
                    public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                        // TODO Auto-generated method stub

                        while (rs.next()) {
                            return rs.getInt("count");
                        }
                        return 0;
                    }
                });
                dao.execute(sql);
                int a2 = (Integer) sql.getResult();
                count2 = count2 + a2;
            } else {
                Sql sql = Sqls.create("select count(1) count from " + t.getTableName() + " where DATA_YEAR between to_number(" + datamonth.replaceAll("-", "") + ") and to_number(" + datamonth1.replaceAll("-", "") + ")");
                sql.setCallback(new SqlCallback() {

                    @Override
                    public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                        // TODO Auto-generated method stub

                        while (rs.next()) {
                            return rs.getInt("count");
                        }
                        return 0;
                    }
                });
                dao.execute(sql);
                int a3 = (Integer) sql.getResult();
                count3 = count3 + a3;
            }
        }
        int count5 = count1 + count2 + count3;
        result.put("count1", count1);
        result.put("count2", count2);
        result.put("count3", count3);
        result.put("count5", count5);
        Map<String, String> dept = DicDataUtils.getInstance().getDic(1069);
        List<Object> list = new ArrayList<Object>();
        for (String key : dept.keySet()) {
            int count4 = 0;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dept", dept.get(key).replaceAll("普陀", ""));
            map.put("deptid", key);
            List<Object> list2 = new ArrayList<Object>();
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> map2 = new HashMap<String, Object>();
                TbContents contents = dao.fetch(TbContents.class, Cnd.where("DEPT_ID", "=", key).and("ORDER_NO", "=", i));
                if (contents != null) {
//					
                    Sql sql1 = Sqls.create("select count(1) count from " + contents.getTableName() + " where DATA_YEAR between to_number(" + datamonth.replaceAll("-", "") + ") and to_number(" + datamonth1.replaceAll("-", "") + ")");
                    sql1.setCallback(new SqlCallback() {

                        @Override
                        public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                            // TODO Auto-generated method stub

                            while (rs.next()) {
                                return rs.getInt("count");
                            }
                            return 0;
                        }
                    });
                    dao.execute(sql1);
                    int a4 = (Integer) sql1.getResult();
                    count4 = count4 + a4;
                    map2.put("scs", String.valueOf(a4));
                    map2.put("tbbm", contents.getName());
//					TbRecord record=dao.fetch(TbRecord.class,Cnd.where("TB_TYPE","=",contents.getName()).and("TB_MONTH","=",datamonth.replaceAll("-", "")).and("ISTB","=","1"));
                    Sql sql = Sqls.create("select count(1) count from tb_record where TB_TYPE = '" + contents.getName() + "' and TB_MONTH between to_number(" + datamonth.replaceAll("-", "") + ") and to_number(" + datamonth1.replaceAll("-", "") + ") AND ISTB= '1'");
                    sql.setCallback(new SqlCallback() {

                        @Override
                        public Object invoke(Connection arg0, ResultSet rs, Sql arg2) throws SQLException {
                            // TODO Auto-generated method stub

                            while (rs.next()) {
                                return rs.getInt("count");
                            }
                            return 0;
                        }
                    });
                    dao.execute(sql);
                    int a = (Integer) sql.getResult();
                    /*if(record==null){*/
                    if (a == 0) {
                        map2.put("sheets", "1");
                    } else {
                        map2.put("sheets", "2");
                    }
                } else {
                    map2.put("tbbm", null);
                    map2.put("sheets", "/");
                }
                list2.add(map2);
            }
            map.put("count4", count4);
            map.put("list", list2);
            list.add(map);
        }
        result.put("roles", roles);
        result.put("list", list);
        return result;
    }

    @At
    @Ok("json")
    public Object getcountdata(String type, String datamon, String datamon1, String deptid) {

        List<Object> result = new ArrayList<Object>();

        List<TbSheet> sheets = dao.query(TbSheet.class, Cnd.where("TABEL_BM", "=", type));
        TbRecord record = dao.fetch(TbRecord.class, Cnd.where("TB_TYPE", "=", type).and("TB_MONTH", "=", datamon.replaceAll("-", "")));

        for (TbSheet sheet : sheets) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sheet", sheet.getSheetName());
            if (record == null && datamon.equalsIgnoreCase(datamon1)) {
                map.put("value", "未填报");
            } else {
                List<TbColumns> columns = dao.query(TbColumns.class, Cnd.where("TABLE_BM", "=", type).
                        and("SHEET_ID", "=", sheet.getOrderNo()).and("IS_WYBS", "is", null));
                if (columns.size() > 0) {
                    Sql sql = Sqls.create("select count(1) from " + columns.get(0).getTableName() +
                            " where " + columns.get(0).getColumnName() + " is not null" +
                            " and DATA_YEAR between to_number(" + datamon.replaceAll("-", "") + ") and to_number(" + datamon1.replaceAll("-", "") + ")");
                    sql.setCallback(new SqlCallback() {
                        @Override
                        public Object invoke(Connection arg0, ResultSet rs, Sql arg2)
                                throws SQLException {
                            if (rs.next()) {
                                return rs.getString(1);
                            }
                            return null;
                        }
                    });
                    dao.execute(sql);
                    map.put("value", sql.getResult());
                } else {
                    map.put("value", "");
                }
            }
            result.add(map);
        }
        return result;
    }

    @At
    @Ok("json")
    public Object getcountdataByDept(String deptid, String datamon, String datamon1) {
        List<Object> result = new ArrayList<Object>();
        Criteria cri = Cnd.cri();
        cri.where().and("DEPT_ID", "=", deptid);
        cri.getOrderBy().asc("ORDER_NO");
        List<TbContents> contents = dao.query(TbContents.class, cri);
        for (int i = 1; i <= 3; i++) {
            boolean flag = false;
            int j = 0;
            for (; j < contents.size(); j++) {
                if (contents.get(j).getOrderNo() == i) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                result.add(getcountdata(contents.get(j).getName(), datamon, datamon1, deptid));
            } else {
                result.add(new ArrayList<Object>());
            }
        }
        return result;
    }

    @At
    @Ok("jsp:jsp.sjtb.countexport")
    public Map<String, Object> exportcount(String datamonth, String datamonth1) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Object> deptcount = new ArrayList<Object>();
        Map<String, String> dept = DicDataUtils.getInstance().getDic(1003);
        for (String key : dept.keySet()) {
            Map<String, Object> deptresult = new HashMap<String, Object>();
            deptresult.put("dept", dept.get(key).replaceAll("普陀", ""));
            List<Object> list = new ArrayList<Object>();//是否填报
            @SuppressWarnings("unchecked")
            List<Object> list2 = (List<Object>) getcountdataByDept(key, datamonth, datamonth1);//各个sheet的数目
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                TbContents contents = dao.fetch(TbContents.class, Cnd.where("DEPT_ID", "=", key).and("ORDER_NO", "=", i));
                if (contents != null) {
                    TbRecord record = dao.fetch(TbRecord.class, Cnd.where("TB_TYPE", "=", contents.getName()).and("TB_MONTH", "=", datamonth.replaceAll("-", "")).and("ISTB", "=", "1"));
                    if (record == null) {
                        map.put("sheets", "未填报");
                    } else {
                        map.put("sheets", "已填报");
                    }
                } else {
                    map.put("sheets", "/");
                }
                list.add(map);
            }
            deptresult.put("tbqk", list);
            deptresult.put("tbxq", list2);
            deptcount.add(deptresult);
        }
        result.put("deptcount", deptcount);
        return result;
    }

    @At
    @Ok("json")
    public Object getindextbyj(HttpSession session) {
        User user = (User) session.getAttribute(SystemConstants.SYSTEM_USER);
        if (user.getRoleId().indexOf("4") == -1) { // 不是填报用户
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        List<TbContents> contents = dao.query(TbContents.class, Cnd.where("DEPT_ID", "=", user.getDept()));
        int count = 0;
        for (TbContents content : contents) {
            TbRecord record = dao.fetch(TbRecord.class, Cnd.where("TB_TYPE", "=", content.getName())
                    .and("TB_MONTH", "=", new SimpleDateFormat("yyyyMM").format(calendar.getTime()))
                    .and("ISTB", "=", "1"));
            if (record == null) {
                count++;
            }
        }
        return count;
    }

    @At
    public void extractallpersondata() {
        dao.execute(Sqls.create("delete from TB_PERSON"));
        List<TbContents> contents = dao.query(TbContents.class, Cnd.where("ORDER_NO", "=", "1"));
        for (final TbContents content : contents) {
            List<TbSheet> sheets = dao.query(TbSheet.class, Cnd.where("TABEL_BM", "=", content.getName()));
            for (final TbSheet sheet : sheets) {
                final TbColumns columns = dao.fetch(TbColumns.class, Cnd.where("TABLE_BM", "=", content.getName()).and("SHEET_ID", "=", sheet.getOrderNo()).and("EXTRACT_FLAG", "=", "1"));
                if (columns != null) {
                    Sql sql = Sqls.create("select ID,DATA_YEAR," + columns.getColumnName() +
                            " zjhm from " + columns.getTableName() + " t where " + columns.getColumnName() + " is not null and id not in ( select "
                            + "TB_DATA_ID from TB_PERSON p where p.TB_DATA_ID = t.ID and "
                            + "t.DATA_YEAR = p.DATAMONTH and p.TABLENAME ='" + columns.getTableBm()
                            + "' )");
                    sql.setCallback(new SqlCallback() {
                        @Override
                        public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                throws SQLException {
                            List<TbPerson> list = new ArrayList<TbPerson>();
                            while (rs.next()) {
                                TbPerson person = new TbPerson();
                                person.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                person.setZjhm(rs.getString("zjhm"));
                                person.setTbdataid(rs.getString("ID"));
                                person.setDeptid(content.getDeptId());
                                person.setSheetid(sheet.getOrderNo() + "");
                                person.setDatamonth(rs.getString("DATA_YEAR"));
                                person.setTablename(columns.getTableBm());
                                person.setSheetname(sheet.getSheetName());
                                list.add(person);
                            }
                            return list;
                        }
                    });
                    dao.execute(sql);
                    @SuppressWarnings("unchecked")
                    List<TbPerson> list = (List<TbPerson>) sql.getResult();
                    for (TbPerson person : list) {
                        dao.insert(person);
                    }
                }
            }
        }
    }

    @At
    public void extractallcorpdata() {
        dao.execute(Sqls.create("delete from TB_CORP"));
        List<TbContents> contents = dao.query(TbContents.class, Cnd.where("ORDER_NO", "=", "2"));
        for (final TbContents content : contents) {
            List<TbSheet> sheets = dao.query(TbSheet.class, Cnd.where("TABEL_BM", "=", content.getName()));
            for (final TbSheet sheet : sheets) {
                Criteria cri = Cnd.cri();
                cri.where().and("TABLE_BM", "=", content.getName());
                cri.where().and("SHEET_ID", "=", sheet.getOrderNo());
                cri.where().andNotIsNull("EXTRACT_FLAG");
                cri.getOrderBy().asc("EXTRACT_FLAG");
                final List<TbColumns> columns = dao.query(TbColumns.class, cri);
                if (columns != null && columns.size() > 0) {
                    StringBuffer sqlstr = new StringBuffer("select ID,DATA_YEAR");
                    for (TbColumns c : columns) {
                        sqlstr.append("," + c.getColumnName());
                    }
                    sqlstr.append(" from " + columns.get(0).getTableName() + " t where (");
                    for (int i = 0; i < columns.size(); i++) {
                        sqlstr.append(columns.get(i).getColumnName() + " is not null ");
                        if (i < columns.size() - 1) {
                            sqlstr.append(" or ");
                        }
                    }
                    sqlstr.append(") and id not in (select TB_DATA_ID from TB_CORP c where c.TB_DATA_ID = t.ID "
                            + " and t.DATA_YEAR = c.DATAMONTH and c.TABLENAME ='" + columns.get(0).getTableBm() + "')");
                    Sql sql = Sqls.create(sqlstr.toString());
                    sql.setCallback(new SqlCallback() {
                        @Override
                        public Object invoke(Connection connection, ResultSet rs, Sql sql)
                                throws SQLException {
                            List<TbCorp> result = new ArrayList<TbCorp>();
                            while (rs.next()) {
                                TbCorp corp = new TbCorp();
                                corp.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                corp.setTbdataid(rs.getString("ID"));
                                corp.setDeptid(content.getDeptId());
                                corp.setSheetid(sheet.getOrderNo() + "");
                                corp.setDatamonth(rs.getString("DATA_YEAR"));
                                corp.setTablename(columns.get(0).getTableBm());
                                corp.setSheetname(sheet.getSheetName());
                                for (TbColumns c : columns) {
                                    if (c.getExtractflag().equals("1")) { //统一信用代码
                                        corp.setShtyxydm(rs.getString(c.getColumnName()));
                                    }
                                    if (c.getExtractflag().equals("2")) { //组织机构代码
                                        corp.setZzjgdm(rs.getString(c.getColumnName()));
                                    }
                                    if (c.getExtractflag().equals("3")) { //营业执照注册号
                                        corp.setYyzzzch(rs.getString(c.getColumnName()));
                                    }
                                    if (c.getExtractflag().equals("4")) { //纳税人识别号
                                        corp.setNsrsbh(rs.getString(c.getColumnName()));
                                    }
                                    if (c.getExtractflag().equals("5")) { //法人名称
                                        corp.setCorpname(rs.getString(c.getColumnName()));
                                    }
                                }
                                result.add(corp);
                            }
                            return result;
                        }
                    });
                    dao.execute(sql);
                    @SuppressWarnings("unchecked")
                    List<TbCorp> list = (List<TbCorp>) sql.getResult();
                    for (TbCorp corp : list) {
                        dao.insert(corp);
                    }
                }
            }
        }
    }

    @At
    @Ok("jsp:jsp.sjtb.person_list")
    public Map<String, Object> getpersondatalist(String zjhm, String dept) {
        Map<String, Object> result = new HashMap<String, Object>();
        Criteria cri = Cnd.cri();
        cri.where().and("ZJHM", "=", zjhm);
        cri.where().and("DEPTID", "=", dept);
        cri.getOrderBy().desc("DATAMONTH");
        List<TbPerson> list = dao.query(TbPerson.class, cri);
        for (TbPerson person : list) {
            String datamon = person.getDatamonth();
            if (!Strings.isEmpty(datamon) && datamon.length() >= 6) {
                person.setDatamonth(datamon.substring(0, 4) + "年" + datamon.substring(4, 6) + "月");
            }
        }
        result.put("list", list);
        return result;
    }

    @At
    @Ok("jsp:jsp.sjtb.corp_list")
    public Map<String, Object> getcorpdatalist(String corpinfoid, String dept) {
        Map<String, Object> result = new HashMap<String, Object>();
        CorpInfo corp = dao.fetch(CorpInfo.class, Cnd.where("CORP_INFO_ID", "=", corpinfoid));
        StringBuffer condstr = new StringBuffer(" DEPTID ='" + dept + "' and (");
        boolean flag = false;
        if (!Strings.isEmpty(corp.getUniscid())) {
            condstr.append((flag ? " or " : "") + " SHTYXYDM = '" + corp.getUniscid() + "' ");
            flag = true;
        }
        if (!Strings.isEmpty(corp.getOrgancode())) {
            condstr.append((flag ? " or " : "") + " ZZJGDM = '" + corp.getOrgancode() + "' ");
            flag = true;
        }
        if (!Strings.isEmpty(corp.getRegno())) {
            condstr.append((flag ? " or " : "") + " YYZZZCH = '" + corp.getRegno() + "' ");
            flag = true;
        }
        if (!Strings.isEmpty(corp.getTaxpayerscode())) {
            condstr.append((flag ? " or " : "") + " NSRSBH = '" + corp.getTaxpayerscode() + "' ");
            flag = true;
        }
        if (!Strings.isEmpty(corp.getCorpname())) {
            condstr.append((flag ? " or " : "") + " CORP_NAME = '" + corp.getCorpname() + "' ");
            flag = true;
        }
        condstr.append(")");
        Condition wrap = Cnd.wrap(condstr.toString());
        List<TbCorp> list = dao.query(TbCorp.class, wrap);
        for (TbCorp c : list) {
            String datamon = c.getDatamonth();
            if (!Strings.isEmpty(datamon) && datamon.length() >= 6) {
                c.setDatamonth(datamon.substring(0, 4) + "年" + datamon.substring(4, 6) + "月");
            }
        }
        result.put("list", list);
        return result;
    }

    @At
    @Ok("json")
    public String gettbstate(HttpSession session) {
        return session.getAttribute("stjbupload").toString();
    }
}
