FROM hseeberger/scala-sbt
# For a Alpine Linux version, comment above and uncomment below:
# FROM 1science/sbt

# Set the working directory to /app
WORKDIR /reminder-notes

# Copy the current directory contents into the container at /app
COPY . /reminder-notes

EXPOSE 80

CMD ["sbt", "run"]