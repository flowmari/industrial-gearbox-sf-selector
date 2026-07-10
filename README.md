# Industrial Gearbox Sizing & Service Factor Selector

[![CI](https://github.com/flowmari/industrial-gearbox-sf-selector/actions/workflows/ci.yml/badge.svg)](https://github.com/flowmari/industrial-gearbox-sf-selector/actions/workflows/ci.yml)

> Industrial gearbox domain knowledge converted into a tested Java/Spring Boot backend API.

A Java Spring Boot portfolio project for generic service factor screening, reduction ratio calculation, design torque calculation, and reducer sizing diagnosis.

This project is based on hands-on experience with industrial gearbox specifications, CAD specification workflows, technical support, and communication with European machinery manufacturers.

The API returns calculation results, factor breakdowns, selection reasons, and engineering risk notes based on machinery operating conditions such as load type, operating hours, start-stop frequency, shock level, torque, speed, and ambient temperature.

## Overview

This is an industrial gearbox selection assistant that translates machinery selection knowledge into testable backend software.

It calculates generic service factors and checks reducer sizing conditions based on real-world machinery parameters.

The implementation focuses on representing industrial selection rules as Java domain logic, REST API contracts, validation rules, automated tests, Dockerized execution, and CI verification.

## Engineering Scope and Safety Notice

This project intentionally does not include manufacturer-specific catalog data or model-number recommendations.

The current version implements a generic engineering screening model for portfolio purposes.

Final reducer selection must always be verified against official manufacturer documentation.

This API is intended to demonstrate software design, validation, test coverage, and industrial domain reasoning. It is not a replacement for manufacturer selection software, certified engineering review, or project-specific mechanical design checks.

## Why This Project

Industrial gearbox selection is not only a simple calculation problem.

In real machinery applications, reducer sizing depends on load type, operating hours, start-stop frequency, shock level, required torque, input speed, output speed, ambient temperature, and safety margin.

This project focuses on converting those selection considerations into calculation logic, validation boundaries, API responses, diagnostic reasoning, and automated quality checks.

## Current Features

* Generic service factor calculation using multiplicative correction factors
* Load, duty-cycle, start-stop, shock, and ambient-temperature factor handling
* Reduction ratio calculation
* Design torque calculation
* Reducer sizing diagnosis
* Factor breakdown in the API response
* Selection reasons explaining how the result was calculated
* Risk notes for operating conditions that may require engineering review
* Bean Validation for unsafe or invalid API inputs
* REST API endpoint for gearbox screening
* JUnit and MockMvc tests
* JaCoCo coverage verification with an 80% minimum threshold
* Dockerized runtime
* GitHub Actions CI for tests, coverage verification, Docker build, and API smoke test
* Deployment health endpoints: `GET /` and `GET /health`

## Quick Start

Run the full local verification:

```bash
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

Start the API locally:

```bash
./gradlew bootRun
```

Check the deployment health endpoint:

```bash
curl http://localhost:8080/health
```

Call the gearbox screening API:

```bash
curl -X POST http://localhost:8080/api/gearbox/selection \
  -H "Content-Type: application/json" \
  -d '{
    "motorPowerKw": 2.2,
    "inputRpm": 1500,
    "outputRpm": 50,
    "requiredTorqueNm": 300,
    "loadType": "MODERATE",
    "operatingHoursPerDay": 12,
    "startsPerHour": 20,
    "shockLevel": "MEDIUM",
    "ambientTemperatureC": 35
  }'
```

Run with Docker:

```bash
docker build -t industrial-gearbox-sf-selector:local .
docker run --rm -p 8080:8080 industrial-gearbox-sf-selector:local
```

## API Documentation

When the application is running locally, the OpenAPI documentation is available at:

* OpenAPI JSON: `/v3/api-docs`
* Swagger UI: `/swagger-ui.html`
* Direct Swagger UI path: `/swagger-ui/index.html`

The OpenAPI documentation is generated from the Spring Boot application and describes the gearbox screening endpoint, request fields, response fields, validation behavior, and health endpoints.

## API Endpoint

```text
POST /api/gearbox/selection
```

Example request:

```json
{
  "motorPowerKw": 2.2,
  "inputRpm": 1500,
  "outputRpm": 50,
  "requiredTorqueNm": 300,
  "loadType": "MODERATE",
  "operatingHoursPerDay": 12,
  "startsPerHour": 20,
  "shockLevel": "MEDIUM",
  "ambientTemperatureC": 35
}
```

Example response:

```json
{
  "reductionRatio": 30.0,
  "serviceFactor": 1.67,
  "designTorqueNm": 501.0,
  "selectionStatus": "SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE",
  "factorBreakdown": {
    "loadFactor": 1.15,
    "dutyCycleFactor": 1.15,
    "startStopFactor": 1.10,
    "shockFactor": 1.15,
    "ambientTemperatureFactor": 1.00
  },
  "selectionReasons": [
    "Reduction ratio was calculated from input rpm and output rpm: 30.0.",
    "Load factor 1.15 was applied for MODERATE load.",
    "Duty-cycle factor 1.15 was applied for 12.0 operating hours per day.",
    "Start-stop factor 1.10 was applied for 20 starts per hour.",
    "Shock factor 1.15 was applied for MEDIUM shock level.",
    "Ambient-temperature factor 1.00 was applied for 35.0 °C.",
    "The resulting generic service factor is 1.67, so the reducer should be rated for at least 501.0 Nm."
  ],
  "riskNotes": [],
  "diagnosis": "Generic screening result: service factor 1.67 gives a design torque of 501.0 Nm. Select a reducer rated for at least this design torque, then verify the final selection against manufacturer documentation."
}
```

## Engineering Risk Diagnosis

For more severe operating conditions, the API returns engineering review notes instead of presenting the result as a simple pass/fail answer.

Example risk conditions include:

* High ambient temperature
* Long daily operating hours
* Frequent start-stop operation
* Heavy load
* High shock level

In those cases, the response may return:

```text
SCREENING_REQUIRES_ENGINEERING_REVIEW
```

This is intentional.

The project is designed to show not only calculation output, but also the ability to identify conditions that require more careful engineering review.

## Validation Examples

The API rejects unsafe or invalid inputs such as:

* `requiredTorqueNm` less than or equal to zero
* `outputRpm` less than or equal to zero
* `operatingHoursPerDay` greater than 24
* negative `startsPerHour`
* ambient temperature outside the supported screening range
* missing `loadType`
* missing `shockLevel`

This makes the project closer to a backend service with realistic input boundaries, rather than a simple calculation script.


## Quality Gates

This project includes automated checks for both domain logic and API behavior.

The current verification flow covers:

* Java domain tests for service factor calculation
* boundary tests for duty-cycle, start-stop, and ambient-temperature thresholds
* MockMvc API tests for successful and invalid requests
* ProblemDetail error responses for invalid API inputs
* JaCoCo coverage verification with an 80% minimum threshold
* Docker image build verification
* GitHub Actions CI for tests, coverage verification, Docker build, and API smoke testing

The boundary tests are especially important because small changes around threshold values can change the service factor, design torque, selection status, and risk notes.

Examples covered by tests include:

* 8.0 hours and 8.1 hours
* 16.0 hours and 16.1 hours
* 10 and 11 starts per hour
* 30 and 31 starts per hour
* 40.0 °C and 40.1 °C
* 50.0 °C and 50.1 °C

## Calculation Model

The generic screening logic is documented in [Calculation Model](docs/CALCULATION_MODEL.md).

It explains the multiplicative service factor model, factor thresholds, risk notes, boundary testing strategy, and why manufacturer-specific catalog data is intentionally excluded.

## Future Scope

Possible future improvements include:

* OpenAPI documentation for easier API review
* a deployed public demo endpoint
* exported screening summaries for technical communication
* additional non-manufacturer-specific checks for mounting environment, operating assumptions, and specification review

CAD and specification workflow experience is relevant background for understanding reducer selection conditions. However, CAD drawing generation, manufacturer-specific dimensional databases, and actual model-number recommendations are intentionally outside the current scope.

## Tech Stack

* Java 17
* Spring Boot
* Gradle
* Bean Validation
* JUnit
* MockMvc
* JaCoCo
* Docker
* GitHub Actions

## Testing and Quality Gate

This project uses automated tests and coverage verification.

```bash
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

The Gradle build verifies a minimum test coverage threshold of 80%.

GitHub Actions runs tests, coverage verification, Docker build, and an API smoke test on each push to `main`.

## Engineering Focus

This project demonstrates the ability to combine industrial gearbox domain knowledge with software engineering.

It shows how machinery selection knowledge can be represented as:

* domain logic
* validation rules
* REST API contracts
* diagnostic response design
* automated tests
* coverage verification
* Dockerized execution
* CI smoke testing

The CAD-related background is used as domain context for understanding specifications, mounting conditions, technical documentation, and real-world selection workflows.

CAD drawing generation, manufacturer-specific dimensional databases, and model-number recommendation logic are intentionally outside the current scope.

## Roadmap

Potential future improvements include:

* simple web UI for entering selection conditions
* OpenAPI / Swagger documentation
* PDF-style selection summary output
* optional project history storage
* deployment to a public cloud environment
* generic IEC/DIN-related filtering notes without manufacturer-specific catalog data
