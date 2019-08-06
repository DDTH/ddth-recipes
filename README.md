# ddth-recipes

DDTH's Commonly Used Recipes.

Project home: [https://github.com/DDTH/ddth-recipes](https://github.com/DDTH/ddth-recipes).

`ddth-recipes` requires Java 11+ since v1.0.0.

## Installation

Latest release version: `1.0.0`. See [RELEASE-NOTES.md](RELEASE-NOTES.md).

Maven dependency:

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>ddth-recipes</artifactId>
	<version>1.0.0</version>
</dependency>
```

## Recipes

- **API Service**: skeleton to build API service (HTTP, Thrift and gRPC). See [API Service recipe documentation](src/main/java/com/github/ddth/recipes/apiservice/README.md).
- **Checkpoint**: save work state for latter resuming. See [checkpoint recipe documentation](src/main/java/com/github/ddth/recipes/checkpoint/README.md).
- **Global**: store/access/share data via a global static class. See [checkpoint recipe documentation](src/main/java/com/github/ddth/recipes/global/README.md).


## License

See LICENSE.txt for details. Copyright (c) 2018-2019 Thanh Ba Nguyen.

Third party libraries are distributed under their own licenses.
