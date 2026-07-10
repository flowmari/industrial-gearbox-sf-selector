# Deployment

This document describes the deployment boundary for the Industrial Gearbox Sizing & Service Factor API.

## Deployment target

The project is prepared for Docker-based deployment as a web service.

The repository includes:

- `Dockerfile` for building the application image
- `render.yaml` for Render Blueprint deployment
- `/health` endpoint for deployment health checks
- `server.port=${PORT:8080}` so the application can use a platform-provided port while keeping local development on port 8080

## Public endpoints after deployment

After deployment, the expected public endpoints are:

- `/`
- `/health`
- `/v3/api-docs`
- `/swagger-ui/index.html`
- `/api/gearbox/selection`

## Scope

This deployment exposes a generic engineering screening API.

It does not add manufacturer-specific catalog data, model recommendations, certified engineering review, or project-specific mechanical design validation.

Final reducer selection must always be verified against official manufacturer documentation.
