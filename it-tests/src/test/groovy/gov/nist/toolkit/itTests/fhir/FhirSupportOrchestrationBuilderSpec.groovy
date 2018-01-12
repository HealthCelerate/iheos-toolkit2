package gov.nist.toolkit.itTests.fhir

import gov.nist.toolkit.itTests.support.FhirSpecification
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.client.PatientDef
import gov.nist.toolkit.services.client.RawResponse
import gov.nist.toolkit.services.server.orchestration.FhirSupportOrchestrationBuilder
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import spock.lang.Shared

class FhirSupportOrchestrationBuilderSpec extends FhirSpecification  {
    @Shared simId
    def userName = 'fhirsupport'

    def setupSpec() {
        startGrizzlyWithFhir('8889')   // sets up Grizzly server on remoteToolkitPort
    }

    FhirSupportOrchestrationBuilder build() {
        FhirSupportOrchestrationRequest request = new FhirSupportOrchestrationRequest()
        request.userName = userName
        request.environmentName = 'test'
        request.useExistingState = false

        FhirSupportOrchestrationBuilder builder = new FhirSupportOrchestrationBuilder(api, session, request)
        simId = new SimId(builder.siteName)
        println "Simid is ${simId}"
        builder
    }

    RawResponse buildTestEnvironment(builder) {
        RawResponse rawResponse = builder.buildTestEnvironment()
        assert rawResponse instanceof FhirSupportOrchestrationResponse
        rawResponse
    }

    def 'test full build' () {
        setup:
        FhirSupportOrchestrationBuilder builder = build()

        when:
        FhirSupportOrchestrationResponse response = buildTestEnvironment(builder)

        then:
        !response.hasError()

        when:
        response.patients.each { PatientDef pd ->
            println "${pd.pid}, ${pd.given}, ${pd.family}, ${pd.url}"
        }

        then:
        response.patients.size() == 1

        when:
        SimDb db = new SimDb(simId)
        File simDbFile = db.getSimDir()
        println "Sim Dir is ${simDbFile}"

        then:
        simDbFile.exists()
        simDbFile.isDirectory()

        when:
        File simIndex = new File(simDbFile, 'simindex')
        println "Looking for ${simIndex}"

        then:
        simIndex.exists()
    }

    def 'delete sim'() {
        when:
        api.deleteSimulator(simId)

        then:
        sleep(5000) // Why we need this -- Problem here is that the Delete request via REST could be still running before we execute the next Create REST command. The PIF Port release timing will be off causing a connection refused error in the Jetty log.
        !api.simulatorExists(simId)
    }

   def 'recreate fhir support sim to test Lucene index folder was created successfully'() {
       setup:
       FhirSupportOrchestrationBuilder builder = build()

       when:
       FhirSupportOrchestrationResponse response = buildTestEnvironment(builder)

       then:
       !response.hasError()

       when:
       response.patients.each { PatientDef pd ->
           println "${pd.pid}, ${pd.given}, ${pd.family}, ${pd.url}"
       }

       then:
       response.patients.size() == 1

       when:
       SimDb db = new SimDb(simId)
       File simDbFile = db.getSimDir()
       println "Sim Dir is ${simDbFile}"

       then:
       simDbFile.exists()
       simDbFile.isDirectory()

       when:
       File simIndex = new File(simDbFile, 'simindex002') // This is to test the Lucene index folder was created successfully after it was deleted (to avoid the file-in-use Windows locking problem).
       println "Looking for ${simIndex}"

       then:
       simIndex.exists()
   }

}
