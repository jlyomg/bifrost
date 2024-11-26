package com.dataour.bifrost.test;

import com.dataour.bifrost.BifrostApplication;
import com.dataour.bifrost.common.util.StringUtils;
import com.dataour.bifrost.domain.BillCategoryDO;
import com.dataour.bifrost.mapper.BillCategoryDOMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.dataour.bifrost.common.util.BigDecimalUtils.toBigDecimal;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BifrostApplication.class)
public class Test extends BaseTest {
    private String codeTemplate = "    /**\n" + "     * @Name: #{name}\n" + "     * @Description: #{description}\n" + "     */\n" + "    public static final String #{codeUpcase} = \"#{code}\";";

    @Autowired
    private BillCategoryDOMapper billCategoryDOMapper;


    /**
     * 生成老收费项Code
     */
    @org.junit.Test
    public void genOldBillCategory() {
        Example example = new Example(BillCategoryDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("id", 200);
        example.orderBy("group");
        example.orderBy("code");
        example.orderBy("name");
        List<BillCategoryDO> billCategoryDOS = billCategoryDOMapper.selectByExample(example);
        List<BillCategoryDO> billCategorys = billCategoryDOS.stream().filter(billCategoryDO -> {
            if (billCategoryDO.getTag() == null || !billCategoryDO.getTag().contains("[\"New\"]")) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        for (BillCategoryDO billCategoryDO : billCategorys) {
            String code = billCategoryDO.getCode();
            String name = billCategoryDO.getName();
            String description = billCategoryDO.getDescription();
            String codeUpcase = code.toUpperCase();
            String result = codeTemplate.replace("#{name}", name).replace("#{description}", description).replace("#{codeUpcase}", codeUpcase).replace("#{code}", code);
            System.out.println(result);
        }
    }

    /**
     * 生成新收费项Code
     */
    @org.junit.Test
    public void genNewBillCategory() {
        Example example = new Example(BillCategoryDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("id", 200);
        example.orderBy("group");
        example.orderBy("code");
        example.orderBy("name");
        List<BillCategoryDO> billCategoryDOS = billCategoryDOMapper.selectByExample(example);
        List<BillCategoryDO> billCategorys = billCategoryDOS.stream().filter(billCategoryDO -> {
            if (billCategoryDO.getTag() != null && billCategoryDO.getTag().contains("[\"New\"]")) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        for (BillCategoryDO billCategoryDO : billCategorys) {
            String code = billCategoryDO.getCode();
            String name = billCategoryDO.getName();
            String description = billCategoryDO.getDescription();
            String codeUpcase = code.toUpperCase();
            String result = codeTemplate.replace("#{name}", name).replace("#{description}", description).replace("#{codeUpcase}", codeUpcase).replace("#{code}", code);
            System.out.println(result);
        }
    }

    @org.junit.Test
    public void test3() {
        Document doc = Jsoup.parse(readHtml("/Users/JASON/Downloads/CSG charging item name/Sheet1.html"));
        Element body = doc.body();
        Date now = new Date();
        Element tbody = body.getElementsByTag("tbody").get(0);
        Elements trs = tbody.getElementsByTag("tr");

        for (Element tr : trs) {
            Elements elements = tr.getElementsByTag("td");
            String name = elements.get(0).text();
            if (StringUtils.isEmpty(name) || "Stock Sorting".equalsIgnoreCase(name) || name.contains("Item Name")) {
                continue;
            }
            BillCategoryDO billCategoryDO = new BillCategoryDO();
            billCategoryDO.setName(name);
            billCategoryDO.setEnName(elements.get(0).text());
            billCategoryDO.setStandardPrice(toBigDecimal(elements.get(1).text()));
            billCategoryDO.setCode(elements.get(2).text());
            billCategoryDO.setUnit(elements.get(3).text());
            billCategoryDO.setGroup(elements.get(4).text());
            billCategoryDO.setReportGroup(elements.get(4).text());
            billCategoryDO.setSapGroup(elements.get(5).text());
            billCategoryDO.setDescription(elements.get(6).text());
            billCategoryDO.setTag("[New]");
            billCategoryDO.setState("Running");
            billCategoryDO.setCost(toBigDecimal("0"));
            billCategoryDO.setEffectiveDate(now);
            Example example = new Example(BillCategoryDO.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("code", billCategoryDO.getCode());
            List<BillCategoryDO> billCategoryDOS = billCategoryDOMapper.selectByExample(example);
            if (billCategoryDOS != null && billCategoryDOS.size() > 0) {
                billCategoryDOS.forEach(billCategory -> {
                    billCategoryDO.setId(billCategory.getId());
                    billCategoryDOMapper.updateByPrimaryKeySelective(billCategoryDO);
                });
            } else {
                billCategoryDOMapper.insertSelective(billCategoryDO);
            }
        }
    }
}
