
import spock.lang.Specification

class SampleSpec extends Specification {
    def "should dispatch event to annotated handler"() {
        expect:
            2 + 2 == 4
    }
}
