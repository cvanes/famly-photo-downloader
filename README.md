## Famly CLI

CLI application for the [Famly](https://famly.co/) nursery app which allows parents to download photos.

## Usage

To download all photos for the previous calendar month:
```
$ ./gradlew photos --args='--email user@example.com --password password
```

To download all photos for a specific date range:
```
$ ./gradlew photos --args='--email user@example.com --password password --start 2021-01-01 --end 2021-01-31'
```