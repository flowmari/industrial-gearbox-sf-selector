# Industrial Gearbox Sizing & Service Factor Selector

> A Java/Spring Boot portfolio project by a developer with hands-on experience in industrial gearbox specifications, selection workflows, and technical communication with European machinery manufacturers.

A Java Spring Boot application for calculating service factors, checking reducer sizing, and diagnosing selection inputs for industrial gearboxes based on load, operating hours, shock, and torque.

## Overview

This is an industrial gearbox selection assistant that calculates service factors and checks reducer sizing conditions based on real-world machinery parameters.

I built it from my previous experience working with European industrial gearbox manufacturers, CAD specifications, and technical communication with overseas engineering teams.

The goal is to translate industrial machinery domain knowledge into testable software logic using Java and Spring Boot.

## Why This Project

Industrial gearbox selection is not only a simple calculation problem.

In real machinery applications, reducer sizing depends on operating conditions such as load type, operating hours, shock level, required torque, input speed, output speed, and safety margin.

This project focuses on turning those selection considerations into a small software tool with clear inputs, calculation logic, validation, and diagnostic output.

## Planned MVP

The first version focuses on a simple selection workflow:

1. Enter machinery operating conditions
2. Calculate the required service factor
3. Calculate the required reduction ratio
4. Calculate the design torque
5. Return a basic reducer sizing diagnosis

## Core Features

* Service factor calculation
* Gear ratio calculation
* Design torque calculation
* Reducer sizing check
* Parameter diagnosis
* English selection summary

## Example Inputs

* Motor power
* Input rpm
* Required output rpm
* Required torque
* Load type
* Operating hours per day
* Starts per hour
* Shock level

## Example Output

* Recommended service factor
* Reduction ratio
* Design torque
* Selection status
* Diagnosis message

Example:

```text
Required torque: 300 Nm
Service factor: 1.7
Design torque: 510 Nm
Diagnosis: Select a reducer rated for at least 510 Nm.
```

## Tech Stack

* Java
* Spring Boot
* Gradle
* JUnit
* Bean Validation
* REST API

## Portfolio Focus

This project demonstrates the ability to combine industrial gearbox domain knowledge with software engineering.

It is not intended to be a manufacturer-specific selection tool.

It is a portfolio project for implementing calculation logic, validation, and diagnostic rules in a testable Java application.
