package org.jspringbot.keyword.expression;

import junitx.util.PrivateAccessor;
import org.apache.commons.lang.StringEscapeUtils;
import org.jspringbot.spring.ApplicationContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/spring-expression.xml"})
public class ExpressionHelperTest {

    @Autowired
    private ELEvaluate evaluator;

    @Autowired
    private ELShouldBeTrue trueEvaluator;

    @Autowired
    private ELShouldBeFalse falseEvaluator;

    @Autowired
    private ELShouldBeEqual equalEvaluator;

    @Autowired
    private ELAddVariable addVariable;

    @Autowired
    private ApplicationContext context;


    private Object evaluate(Object... values) throws Exception {
        return evaluator.execute(values);
    }

    private void evaluateEquals(Object... values) throws Exception {
        equalEvaluator.execute(values);
    }

    private void addVariable(String name, Object value) throws Exception {
        addVariable.execute(new Object[] {name, value});
    }

    private void evaluateAsTrue(Object... values) throws Exception {
        trueEvaluator.execute(values);
    }

    private void evaluateAsFalse(Object... values) throws Exception {
        falseEvaluator.execute(values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedFormat() throws Exception {
        evaluate("unsupported");
    }

    @Test
    public void testEvaluate() throws Exception {
        assertEquals(2L, evaluate("$[1+1]"));
        evaluateAsTrue("$[contains('alvin', 'vin')]");
        evaluateAsFalse("$[containsNone('alvin', 've')]");
        evaluateEquals("$[100]", "$[100]");
        evaluateEquals("$[i:'100']", 100);
        evaluateEquals("$[l:'100']", 100l);
        evaluateEquals("$[d:'100']", 100.0);
        evaluateEquals("$[f:'100']", 100.0f);
        evaluateEquals("$[s:100]", "100");
        evaluateEquals("$[b:'false']", false);
        evaluateEquals("$[i:math:absLong(-1)]", 1);

        List<String> items = new ArrayList<String>();

        addVariable("items", items);
        evaluateAsTrue("$[col:isEmpty(items)]");
        evaluateAsFalse("$[col:isNotEmpty(items)]");

        evaluateAsTrue("$[col:isEmpty($1)]", items);
        evaluateAsFalse("$[col:isNotEmpty($1)]", items);

        items.add("test");

        evaluateAsTrue("$[col:isNotEmpty(items)]");
        evaluateAsTrue("$[col:isNotEmpty($1)]", items);
        evaluateEquals("$[col:size(items)]", 1);

        items.add("test2");

        evaluateEquals("$[col:size(items)]", 2);
    }

    @Test
    public void testEval() throws Exception {
        evaluateEquals("$[l:eval('$[i:100]')]", 100l);

        List<String> items = new ArrayList<String>();
        items.add("test");

        evaluateEquals("$[f:eval('$[col:size($1)]')]", 1.0f, items);
        evaluateEquals("$[eval('$[$2 - $3]', 6, 5) eq col:size($1)]", true, items);
    }

    @Test
    public void testVArgsAndMethod() throws Exception {
        evaluateEquals("$[concat('1', '2', '3')]", "123");
        evaluateEquals("$[join('-', '1', '2', '3')]", "1-2-3");
        evaluateEquals("$[substring('alvin', 2)]", "vin");
        evaluateEquals("$[substring('alvin', 2, 4)]", "vi");
        evaluateEquals("$['alvin'.length()]", 5);
        evaluateEquals("$[b:concat('tr', 'ue')]", true);
    }

    @Test
    public void testStringFunction() throws Exception {

        evaluateEquals("$[splitByWholeSeparator('20.00/50,000.00','/')[0]]", "20.00");
        evaluateEquals("$[splitByWholeSeparator('20.00/50,000.00','/')[1]]", "50,000.00");

    }

    @Test
    public void testMd5() throws Exception {
        evaluateEquals("$[md5('12345678')]", "25d55ad283aa400af464c76d713c07ad");
    }

    @Test
    public void testNesting() throws Exception {
        evaluateEquals("$[i:l:f:d:5.6]", 5);
    }

    @Test
    public void testIn() throws Exception {
        evaluateAsTrue("$[in('alvin', 'hello', 'world', 'alvin')]");
        evaluateAsFalse("$[in('alvin', 'hello', 'world')]");
    }

    @Test
    public void testDoCase() throws Exception {
        evaluateEquals("$[doCase('var' eq 'var', 'value')]", "value");
        evaluateEquals("$[doCase('var' eq 'not var', 'var', 'var' eq 'var', 'next var')]", "next var");
        evaluateEquals("$[doCase('var' eq 'not var', 'var', 'var' eq 'not var', 'next var', 'default')]", "default");
    }

    @Test
    public void testDoMap() throws Exception {
        evaluateEquals("$[doMap('var', 'var', '1')]", "1");
        evaluateEquals("$[doMap('var', 'hello', '1', 'var', '2')]", "2");
        evaluateEquals("$[doMap('var', 'hello', '1', 'world', '2', '5')]", "5");
    }

    @Test
    public void testConvertUnicode() throws Exception {
        evaluateEquals("$[convertUnicode('\\\\u632F\\\\u534E')]", "\u632F\u534E");
        System.out.println(ELUtils.convertUnicode("\\u767B\\u5165\\u60A8\\u7684\\u535A\\u72D7\\u8D26\\u6237"));
    }

    @Test
    public void testCreateStringFunctionsXML() throws Exception {
        new ClassStaticFunctionsPrinter(StringEscapeUtils.class).addPrefix("escape:").print(System.out);
    }

    @Test
    public void testRegexCapture() throws Exception {
        //evaluateEquals("$[i:regexCapture('display: inline-block; transform: rotate(990deg);', 'rotate\\\\(([0-9]+)deg\\\\)', 1)]", 990);
        evaluateEquals("$[i:regexCapture('display: inline-block; transform: rotate(990deg);', 'rotate.([0-9]+)deg.', 1)]", 990);
        evaluateEquals("$[i:360-(990 mod 360)]", 90);
        evaluateEquals("$[i:360-(1080 mod 360)]", 360);
    }

    @Test
    public void testGreaterThan() throws Exception {
        evaluateAsTrue("$[2 > 1]");
        evaluateAsFalse("$[1 > 2]");
        evaluateAsTrue("$['amschartstusr001n179' < 'å??è??']");
    }
    
    @Test
    public void testGetJSFileLinks() throws Exception {
    	List<String> links = new ArrayList<String>();
    	links.add("https://foo.bar/file.js");
    	
    	evaluateEquals("$[parser:getJsFileLinksInHTML('<html><script type=\"text/javascript\" src=\"https://foo.bar/file.js\"></script></html>')]", links);
    }

    @Test
    public void testShuffle() throws Exception {
        List<String> list = Arrays.asList("1", "2", "3");

        addVariable("list", list);
        evaluate("$[col:shuffle(list)]");

        ListIterator<String> itr = (ListIterator<String>) evaluate("$[list.listIterator()]");

        addVariable("itr", itr);

        System.out.println(list);

        System.out.println("Next: " + evaluate("$[itr.next()]"));
        System.out.println("Next: " + evaluate("$[itr.next()]"));
        System.out.println("Next: " + evaluate("$[itr.next()]"));

    }
    
    @Test
    public void testLoremIpsum() throws Exception {
    	String expectedText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    	evaluateEquals("$[faker:getLoremParagraphs(1)]", expectedText);
    }
    
    @Test
    public void testURLGetPath() throws Exception {
    	String expectedPath = "/path/to/url/";
    	evaluateEquals("$[parser:getUrlPath('http://www.example.com/path/to/url/')]", expectedPath);
    }
    
    @Test
    public void testURLGetHost() throws Exception {
    	String expectedHost = "www.google.com";
        evaluateEquals("$[parser:getUrlHost('http://www.google.com')]", expectedHost);
    }
    
    @Test
    public void testGetCSSFileLinks() throws Exception {
    	List<String> links = new ArrayList<String>();
    	links.add("https://foo.bar/file.css");
    	
    	evaluateEquals("$[parser:getCssFileLinksInHTML('<html><link type=\"text/css\" rel=\"stylesheet\" href=\"https://foo.bar/file.css\" media=\"all\" /></html>')]", links);
    }

    @Test
    public void testGenerateUUID() throws Exception {

        assertTrue(((String) evaluate("$[generateUUID()]")).matches("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}"));

    }

    @Test
    public void testRandomBeetweenFloat() throws Exception {
        addVariable("randomFloat", evaluate("$[math:randomBetweenFloat(0.01,0.50)]"));
        evaluate("$[randomFloat < 0.50]");
        System.out.println("randomFloat: " + evaluate("$[randomFloat]"));
    }

    @Test
    public void testRandomBeetweenDouble() throws Exception {
        addVariable("randomDouble", evaluate("$[math:randomBetweenDouble(0.01,0.50)]"));
        evaluate("$[randomDouble < 1]");
        System.out.println("randomDouble: " + evaluate("$[randomDouble]"));
    }

    @Test
    public void testFormatDouble() throws Exception {
        addVariable("formatDouble", "108.27000000000001");
        evaluate("$[formatDouble(formatDouble,'##.00')]");
        System.out.println("formaDouble: " + evaluate("$[formatDouble(formatDouble,'##.00')]"));
        evaluateEquals(108.27,"$[formatDouble(formatDouble,'##.00')]");
    }

    @Test
    @Ignore
    public void testBase64Image() throws Exception {
        // Works for file and classpath
        // Sample A:
        // ${imgFile}=       Element Capture Screenshot         $[config:captcha.image.locator]
        // EL Add Variable   base64String                        $[base64Image('file:${imgFile}','png')]
        // evaluate("$[base64Image('file:${imgFile}','png')]");

        // Sample B: captcha.png in images folder
        // evaluate("$[base64Image('classpath:images/captcha.png','png')]");
        // evaluate("$[base64Image('/Users/shielabuitizon/Downloads/Captcha.png','png')]");
        // System.out.println("base64Image: " + evaluate("$[basei64Image('/Users/shielabuitizon/Downloads/Captcha.png','png')]"));

        evaluate("$[base64Image('classpath:images/captcha.jpg', 'jpeg')]");
        System.out.println("base64Image: " + evaluate("$[base64Image('classpath:images/captcha.jpg', 'jpeg')]"));
        //evaluate("$[base64Image('file::C:\\workspace\\qat-w88-web\\target\\robotframework-reports\\screen_capture_1566531624359_1.png', 'png')]");
        //System.out.println("base64Image: " + ELUtils.base64Image("file:C:\\workspace\\qat-w88-web\\target\\robotframework-reports\\screen_capture_1566531624359_1.png", "png"));
        //evaluate("$[base64Image('file::C:/workspace/qat-w88-web/target/robotframework-reports/screen_capture_1566531624359_1.png', 'png')]");
        //System.out.println("base64Image: " + ELUtils.base64Image("file:C:/workspace/qat-w88-web/target/robotframework-reports/screen_capture_1566531624359_1.png", "png"));
    }

    @Before
    public void setUp() throws Throwable {
        PrivateAccessor.invoke(ApplicationContextHolder.class, "set", new Class[] {ApplicationContext.class}, new Object[] {context});
    }

    @After
    public void tearDown() throws Throwable {
        PrivateAccessor.invoke(ApplicationContextHolder.class, "remove", new Class[] {}, new Object[] {});
    }
}
