package com.dataour.bifrost.test;

import com.dataour.bifrost.domain.BillCategoryDO;
import com.dataour.bifrost.mapper.BillCategoryDOMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.dataour.bifrost.common.util.BigDecimalUtils.toBigDecimal;


public class ParseHtml extends BaseTest {
    @Autowired
    private BillCategoryDOMapper billCategoryDOMapper;


    @Test
    public void test1() {
        String codeTemplate = "    /**\n" + "     * @Name: #{name}\n" + "     * @Description: #{description}\n" + "     */\n" + "    public static final String #{codeUpcase} = \"#{code}\";";

//        Document doc = Jsoup.parseBodyFragment(html);
        Document doc = Jsoup.parse(readHtml("/Users/JASON/IdeaProjects/bifrost/bifrost-service/src/main/test/com/dataour/bifrost/test/a.html"));
        Element body = doc.body();
        body.getElementsByTag("tr").forEach(p -> {
            Elements tds = p.getElementsByTag("td");
            if (tds.size() == 0) {
                return;
            }
            BillCategoryDO billCategoryDO = new BillCategoryDO();
            billCategoryDO.setName(tds.get(0).text());
            billCategoryDO.setEnName(tds.get(0).text());
            billCategoryDO.setStandardPrice(toBigDecimal(tds.get(1).text().replace("$", "")));
            billCategoryDO.setCost(toBigDecimal(tds.get(2).text().replace("$", "")));
            billCategoryDO.setCode(tds.get(3).getAllElements().get(2).text());
            billCategoryDO.setSapGroup(tds.get(4).text());
            billCategoryDO.setReportGroup(tds.get(5).text());
            billCategoryDO.setDescription(tds.get(6).text());
            billCategoryDO.setUnit(getUnitByName(billCategoryDO.getName()));
            billCategoryDO.setGroup(getGroup(billCategoryDO.getReportGroup()));
            System.out.println(codeTemplate.replace("#{name}", billCategoryDO.getName()).replace("#{description}", billCategoryDO.getDescription()).replace("#{codeUpcase}", billCategoryDO.getCode().toUpperCase()).replace("#{code}", billCategoryDO.getCode()));
//            billCategoryDOMapper.insertSelective(ParamUtils.fillAddParams(billCategoryDO));
        });
    }

    @Test
    public void test2() {
        Document doc = Jsoup.parse(readHtml("/Users/JASON/IdeaProjects/bifrost/bifrost-service/src/main/test/com/dataour/bifrost/test/b.html"));
        Element body = doc.body();
        StringBuffer sb = new StringBuffer();
        body.getElementsByClass("v-chip__content").forEach(p -> {
            sb.append("'" + p.text() + "'").append(",");
        });
        System.out.println(sb.toString());
    }
}
