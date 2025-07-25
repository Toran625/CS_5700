package org.example.project

import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    AbstractShipmentTest::class,
    StandardShipmentTest::class,
    ExpressShipmentTest::class,
    OvernightShipmentTest::class,
    BulkShipmentTest::class,
    ShipmentUpdateTest::class,
    UpdateMethodsTest::class,
    TrackingServerTest::class,
    TrackerViewHelperTest::class,
    TimeUtilityTest::class,
    IntegrationTest::class,
    ErrorHandlingTest::class,
    //EdgeCaseTest::class
)
class AllTestsSuite