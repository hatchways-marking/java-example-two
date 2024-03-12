# Getting Started

- System requirements
    - JDK 17
    - Gradle
- Start the dev server.
  ```
  gradle bootRun
  ```

# Getting Started (Docker)

Instead of following the steps above, you can also use Docker to set up your environment.

- System requirements
    - [Docker Compose](https://docs.docker.com/compose/install/)
- Run `docker-compose up` to spin up the dev server.
- Enter `Ctrl-C` in the same same terminal or `docker-compose down` in a separate terminal to shut down the server.

# Verify That Everything Is Set Up Correctly

You can use cURL or a tool like [Postman](https://www.postman.com/) to test the API.

#### Example Curl Commands

You can log in as one of the seeded users with the following curl command:

```bash
curl --location --request POST 'localhost:8080/api/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "thomas",
    "password": "123456"
}'
```

Then you can use the token that comes back from the /login request to make an authenticated request to create a new blog
post

```bash
curl --location --request POST 'localhost:8080/api/posts' \
--header 'x-access-token: your-token-here' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "This is some text for the blog post...",
    "tags": ["travel", "hotel"]
}'
```

# Helpful Commands

- This project is formatted using [google-java-format](https://github.com/google/google-java-format). If you are using
  IntelliJ IDEA, you can use this [plugin](https://plugins.jetbrains.com/plugin/8527-google-java-format/)

# Common Setup Errors

- If you see `gradle bootRun` get stuck with a message "80% EXECUTING", please check if there is a log message "Started
  BlogPostsApplication in \*.\*\* seconds". Your server is running is you see this log.
