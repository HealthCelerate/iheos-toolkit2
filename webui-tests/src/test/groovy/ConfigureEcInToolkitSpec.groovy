import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput
import gov.nist.toolkit.webUITests.confActor.ToolkitWebPage
import spock.lang.Stepwise
import spock.lang.Timeout

@Stepwise
@Timeout(360)
class ConfigureEcInToolkitSpec extends ToolkitWebPage {

    def setupSpec() {
        loadPage(String.format("%s/",toolkitBaseUrl))

    }

    def 'Login to the Configure Toolkit admin page.'() {
        when:
        String ecDir = findItTestEcDir()

        then:
        ecDir!=null

        when:
        HtmlAnchor toolkitConfigAnchor = findAnchor("Toolkit configuration")

        then:
        toolkitConfigAnchor  != null

        when:
        page = toolkitConfigAnchor.click()
        webClient.waitForBackgroundJavaScript(2000)

        then:
//        page.asText().contains("Test Context")
        List<HtmlPasswordInput> pwInputs = page.getByXPath("//input[contains(@class, 'gwt-PasswordTextBox')]")  // Substring match. No other CSS class must contain this string.
        listHasOnlyOneItem(pwInputs)

        when:
        HtmlPasswordInput pwInput = pwInputs.get(0)
        pwInput!=null

        // There is a problem with Installation running outside of the web servlet environment
//        Installation.setTestRunning(true)
//        println "---" + Installation.instance()==null
//        println "---" + Installation.instance().propertyServiceManager()==null
//        println "---" + Installation.instance().propertyServiceManager().getTestLogCache()
//        println "---" + Installation.instance().propertyServiceManager().getPropertyManager() // Load toolkit.properties only for the password
//        String pw = Installation.instance().propertyServiceManager().getPropertyManager().getPassword()
//        println pw

        pwInput.setValueAttribute(toolkitPassword)
        List<HtmlButton> okButtonList = page.getByXPath("//button[contains(@class,'gwt-Button') and text()='Ok']")

        then:
        listHasOnlyOneItem(okButtonList)

        when:
        page = okButtonList.get(0).click() // Enter key doesn't work here.

        then:
        page.asText().contains("Configure XDS Toolkit")
        println "Got Configure XDS Toolkit page as expected"
    }

    def 'Update Ec dir.'() {
        page.get
    }
}
