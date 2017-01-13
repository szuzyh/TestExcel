import http.CommonOperate;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Leo on 2017/1/13.
 */
public class Test {

    static String api_key="pPcCQavGfltqRq6m8vIFgALMpmaS3BhI";
    static String api_secret="dbwoZSx8BUYRIwQudeSxZiab7WnQEhST";
    static CommonOperate commonOperate=new CommonOperate(api_key,api_secret);

    public static void main(String[] args) throws Exception {
        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();
//建立新的sheet对象（excel的表单）
        HSSFSheet sheet=wb.createSheet("相似度");
//在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        HSSFRow row1=sheet.createRow(0);
//创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cell=row1.createCell(0);
        //设置单元格内容
        cell.setCellValue("相似度一览");
//合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));

        for (int i=1;i<=10;i++){
            HSSFRow row2=sheet.createRow(i+2);
            for(int j=1;j<=10;j++){
                File file1=new File("own"+i+".jpg");
                File file2=new File("p"+j+".jpg");
                double confindence=compareFace(file1,file2);
                System.out.println("第"+i+"列第"+j+"个对比相似度为："+confindence);
                row2.createCell(j+2).setCellValue(confindence);
            }
        }
        FileOutputStream output=new FileOutputStream("E:\\相似度.xls");
        wb.write(output);
        output.flush();
    }
    private static String resultGetFaceToken(byte[] result) throws JSONException {
        String s=new String(result);
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = (JSONArray) jsonObject.get("faces");
        JSONObject object = jsonArray.optJSONObject(0);
        if (object==null) {
            return null;
        }else {
            String str = object.getString("face_token");
            return str;
        }
    }
    private static double compareFace(File file1, File file2) throws Exception {

        byte[] result1= commonOperate.detectFile(file1);
        String token1= resultGetFaceToken(result1);
        if (token1==null){
            return 1;
        }
        byte[] result2=commonOperate.detectFile(file2);
        String token2= resultGetFaceToken(result2);
        if (token2==null){
            return 1;
        }
        byte[] compareResult=commonOperate.compare(token1,token2);
        String s=new String(compareResult);
        JSONObject jsonObject=new JSONObject(s);
        JSONObject levelObject=jsonObject.getJSONObject("thresholds");
        int le_3=levelObject.getInt("1e-3");
        int le_4=levelObject.getInt("1e-3");
        int le_5=levelObject.getInt("1e-3");
        double confidence= jsonObject.getDouble("confidence");


            return confidence;

    }

}
