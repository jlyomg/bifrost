package com.dataour.bifrost.test;

import com.dataour.bifrost.BifrostApplication;
import com.dataour.bifrost.common.enums.BillCategoryUnitEnum;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BifrostApplication.class})
public class BaseTest {
    /**
     * 读html文件
     *
     * @param fileName
     * @return
     */
    public static String readHtml(String fileName) {
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();
        try {
            fis = new FileInputStream(fileName);
            byte[] bytes = new byte[1024];
            while (-1 != fis.read(bytes)) {
                sb.append(new String(bytes));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String getGroup(String value) {
        if ("LABOUR".equalsIgnoreCase(value)) {
            return "VAS";
        } else {
            return value;
        }
    }

    public String getUnitByName(String name) {
        if (name.contains("kg ") || name.contains("kg)")) {
            return BillCategoryUnitEnum.Kg.getCode();
        } else if (name.contains("packs")) {
            return BillCategoryUnitEnum.ctn_pc.getCode();
        } else {
            return null;
        }
    }
}
