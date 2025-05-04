FROM eclipse-temurin:17-jdk AS build

# Устанавливаем Gradle в определенную версию
ENV GRADLE_VERSION=8.5
ENV GRADLE_HOME=/opt/gradle
ENV PATH=${GRADLE_HOME}/bin:${PATH}

# Загружаем и устанавливаем Gradle
RUN apt-get update && apt-get install -y --no-install-recommends \
    unzip \
    wget \
    && rm -rf /var/lib/apt/lists/* \
    && wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -O gradle.zip \
    && mkdir -p ${GRADLE_HOME} \
    && unzip -d /opt gradle.zip \
    && rm gradle.zip \
    && mv /opt/gradle-${GRADLE_VERSION}/* ${GRADLE_HOME} \
    && rm -rf /opt/gradle-${GRADLE_VERSION}

WORKDIR /app

# Копируем файлы конфигурации Gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Скачиваем зависимости
RUN gradle --version && \
    gradle dependencies --no-daemon || echo "Зависимости не загружены, но продолжаем сборку"

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN gradle build --no-daemon -x test

# Проверяем содержимое директории с JAR-файлом
RUN ls /app/build/libs

# Второй этап - создание образа для запуска
FROM eclipse-temurin:17-jre

WORKDIR /app

# Копирование jar файла в отдельную директорию
COPY --from=build /app/build/libs /app/libs

# Проверяем содержимое директории libs
RUN ls /app/libs

# Перемещение jar файла в корень рабочей директории
RUN mkdir -p /app && mv /app/libs/lottery-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
