# Calculation Model

This document explains the generic engineering screening model used by the Industrial Gearbox Sizing & Service Factor Selector.

The goal of this project is not to reproduce any manufacturer catalog or recommend real model numbers. The goal is to show how industrial gearbox screening logic can be represented as testable Java domain logic, REST API responses, validation rules, diagnostic messages, and automated tests.

## Scope

This project intentionally does not include:

* manufacturer-specific catalog data
* manufacturer-specific service factor tables
* actual gearbox model-number recommendations
* certified mechanical design validation
* project-specific thermal, bearing-life, shaft-load, or mounting checks

The current implementation is a generic engineering screening model for demonstrating software design and domain modeling.

Final reducer selection must always be verified against official manufacturer documentation.

## Inputs Used by the Model

The current API uses the following inputs:

* motor power in kW
* input speed in rpm
* output speed in rpm
* required torque in Nm
* load type
* operating hours per day
* starts per hour
* shock level
* ambient temperature in Celsius

These inputs represent common operating conditions that affect reducer sizing discussions in industrial machinery applications.

## Calculated Outputs

The API currently returns:

* reduction ratio
* generic service factor
* design torque
* selection status
* factor breakdown
* selection reasons
* risk notes
* diagnosis message

This is intentionally more than a simple numeric calculator. The response is designed to explain how the result was produced and when engineering review is recommended.

## Reduction Ratio

The reduction ratio is calculated from input speed and output speed.

```text
reductionRatio = inputRpm / outputRpm
```

Example:

```text
1500 rpm / 50 rpm = 30.0
```

## Generic Service Factor

The current model uses a multiplicative service factor approach.

```text
serviceFactor =
    loadFactor
    * dutyCycleFactor
    * startStopFactor
    * shockFactor
    * ambientTemperatureFactor
```

This is a deliberate design choice.

A simple additive model can understate how multiple operating conditions interact. A multiplicative model makes the screening result more conservative when several demanding conditions are present at the same time, such as long operating hours, frequent start-stop operation, high shock, and elevated ambient temperature.

This does not claim to replace manufacturer service factor tables. It is a generic engineering screening approach used to make the domain logic explicit, testable, and reviewable.

## Factor Breakdown

The current factor table is intentionally simple and generic.

| Condition | Range | Factor |
|---|---:|---:|
| Load type | LIGHT | 1.00 |
| Load type | MODERATE | 1.15 |
| Load type | HEAVY | 1.30 |
| Operating hours per day | 0 < hours <= 8 | 1.00 |
| Operating hours per day | 8 < hours <= 16 | 1.15 |
| Operating hours per day | 16 < hours <= 24 | 1.30 |
| Starts per hour | 0 to 10 | 1.00 |
| Starts per hour | 11 to 30 | 1.10 |
| Starts per hour | 31 or more | 1.20 |
| Shock level | LOW | 1.00 |
| Shock level | MEDIUM | 1.15 |
| Shock level | HIGH | 1.30 |
| Ambient temperature | <= 40 °C | 1.00 |
| Ambient temperature | > 40 °C and <= 50 °C | 1.10 |
| Ambient temperature | > 50 °C | 1.20 |

These values are not manufacturer catalog values. They are generic screening factors used to make the calculation logic explicit, testable, and reviewable.

## Design Torque

The design torque is calculated by multiplying the required torque by the generic service factor.

```text
designTorqueNm = requiredTorqueNm * serviceFactor
```

Example:

```text
300 Nm * 1.67 = 501.0 Nm
```

The API then explains that the reducer should be rated for at least the calculated design torque, before final verification against manufacturer documentation.

## Selection Status

The API currently returns one of two screening statuses.

```text
SCREENING_OK_SELECT_REDUCER_RATED_FOR_DESIGN_TORQUE
```

This means no additional risk notes were detected by the generic screening rules.

```text
SCREENING_REQUIRES_ENGINEERING_REVIEW
```

This means one or more operating conditions require additional attention before final reducer selection.

## Risk Notes

Risk notes are returned when the input conditions indicate that a simple screening result may not be enough.

Examples include:

* ambient temperature above 40 °C
* ambient temperature above 50 °C
* long daily operating hours
* frequent start-stop operation
* heavy load
* high shock level

The point is not to automatically reject a selection. The point is to surface conditions that should be checked carefully against manufacturer documentation.

## Boundary Testing Strategy

The test suite covers boundary conditions where the screening factors change.

Examples:

* 8.0 hours and 8.1 hours
* 16.0 hours and 16.1 hours
* 10 and 11 starts per hour
* 30 and 31 starts per hour
* 40.0 °C and 40.1 °C
* 50.0 °C and 50.1 °C

This is important because small changes around threshold values can change the service factor, design torque, selection status, and risk notes.

## Why Manufacturer Catalog Data Is Excluded

Manufacturer catalog data is intentionally excluded for several reasons.

First, each manufacturer has its own selection method, rating tables, thermal limits, mounting factors, and service factor recommendations.

Second, catalog data may be copyrighted, licensed, or restricted.

Third, adding actual model-number recommendations would shift this project from a generic screening tool into a manufacturer-specific selection system, which would require careful validation and manufacturer-specific engineering review.

For those reasons, this project stays at the generic engineering screening layer.

## Engineering Interpretation

This project should be understood as a software engineering demonstration based on industrial machinery knowledge.

It shows how domain knowledge can be represented as:

* explicit calculation rules
* validated inputs
* factor breakdowns
* selection reasons
* risk notes
* boundary tests
* API error handling
* CI-verified Java code

The intended message is:

```text
Industrial gearbox screening knowledge converted into a tested Java API.
```

## Engineering Review Checklist

The engineering review checklist is a generic final-verification aid returned with the screening response.

It does not generate CAD drawings, BOMs, manufacturer-specific catalog data, or model-number recommendations. Instead, it lists review boundaries that should be checked before final reducer selection, such as:

* official manufacturer documentation
* mounting position, shaft orientation, and installation constraints
* coupling, motor, and driven-machine interfaces
* thermal rating, lubricant recommendation, and ambient-temperature derating
* bearing-life assumptions for long daily operating hours
* start-stop duty and motor starting behavior
* shock loading, shaft loads, and application-specific safety margin
