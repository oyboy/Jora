# Jora

Система управления проектами.

## Как запустить

Для запуска потребуется Docker Compose. [Установка Docker Compose](https://docs.docker.com/compose/install/).

### Шаг 1: Клонирование репозитория

Клонируйте репозиторий и перейдите в него:

```bash
git clone https://github.com/oyboy/Jora.git
cd jora
```

### Шаг 2
Скопируйте файл `.env.sample` в файл `.env`:

Для Linux: 
```bash
cp .env.sample .env
```
Для Windows: 
```cmd
copy .env.sample .env
```

Откройте файл .env и измените значения переменных на свои собственные:
```ini
MYSQL_DATABASE=your_database_name # Название вашей базы данных
MYSQL_ROOT_PASSWORD=your_root_password # Пароль для пользователя root
DATASOURCE_USERNAME=your_username # Имя пользователя для доступа к приложению
DATASOURCE_PASSWORD=your_password # Пароль для доступа к приложению
```
### Шаг 3
Запустите!

В Linux: 
```bash
sudo docker compose up
```
В Windows: 
```cmd
docker-compose up
```
