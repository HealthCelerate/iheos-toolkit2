package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared

/**
 *
 */
class ResDbTest extends FhirSpecification {
    @Shared SimId simId = new SimId('default', 'test')

    def 'build/delete fhir sim'() {
        when:
        SimDb.fdelete(simId)  // just in case

        new SimDb().mkfSim(simId)

        then:
        SimDb.fexists(simId)

        when:
        SimDb.fdelete(simId)

        then:
        !SimDb.fexists(simId)
    }
}