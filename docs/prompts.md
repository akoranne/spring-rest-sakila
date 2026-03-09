This file lists prompts used to assess, and modernize the application

## Settings
- ide: kiro
- model: 
  - set to Auto with available models
  - Claude Sonnet 4.6 (1.3x credit)
  - Claude Opus 4.6 (2.2x credit)
  - Claude Opus 4.6 1M token context window (2.2x credit)
  - and host of other models

## Prompts

1. Review the current project.
Review the current project.
- assess the nature of the application
- assess the status of the testing - unit testing, integration testing, non-functional testing
- assess status of ci/cd pipelines
- write assessment findings to `docs/initial_assessment.md`

2. Create archives folder
- move legacy readme files to `archives` folder


3. Identify domains
- start with technical domains
- then identify the business domains
- write the analysis findings to `docs/ddd_assessment`

4. Create new spec
- modernize the legacy enterprise system with following strategies
- target architecture 
  - from monolith to microservices architecture
  - follow the business domains to create bounded services
  - convert services from java to node.js
  - services should follow cloud native patterns
- data migration 
  - db to use will RDS/Postgresql
  - convert queries for postgres
  - db/schemas should be bounded to the domain (no services can directly access a db across domain boundaries)
- ci/cd
  - create container images for each service
- testing 
  - add detail tests - unit tests, integration tests
- deployment
  - test with docker compose
  - eventually will deploy to ECS

5. Make sure the old code remains as is. 
- create a new workspace where each service will be its own git project.


6. manual step:: validated and verified requirements generated (part of spec driven development)
  
7. Generate design

8. Generate tasks
- notes 
  - Jira stories can be created for the tasks. I have not implemented it here.)
  - validated tasks

9. Generate code
- ran step 1
- ran step 2.1, 2.2, 2.3
- ran step 4

10. Move all legacy source to `archives` folder

11. Add a readme file with steps on how to run the microservices application.
- add the file under `sakila-microservices` workspace only
12. Add Dockerfile for each services
13. Add a CI/CD pipeline(s) under under `sakila-microservices` workspace only 
14. Update readme for steps to create the docker image to run with docker compose.
- include steps on how to run ci.yml, and cd.yml.








--
1. Generate Steering Docs
- to create and integrate agents
  

