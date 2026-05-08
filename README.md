# Library Spring

Projekt przedstawia prosta aplikacje biblioteczna napisana w Spring Boot. Backend udostepnia REST API do zarzadzania bibliotekami, ksiazkami, uzytkownikami oraz wypozyczeniami. Do projektu przygotowany jest rowniez prosty frontend testowy w czystym HTML, CSS i JavaScript.

## Testowy frontend

Do projektu został przygotowany prosty frontend testowy, który umożliwia wygodne sprawdzanie działania endpointów backendu.

Repozytorium frontendu: [https://github.com/macieserafin/library-management-frontend](https://github.com/macieserafin/library-management-frontend)



## Technologie

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Docker i Docker Compose
- Frontend: HTML, CSS, JavaScript, Nginx

## Funkcjonalnosci

Aplikacja pozwala na:

- dodawanie, edycje, usuwanie i wyswietlanie bibliotek,
- dodawanie, edycje, usuwanie, wyswietlanie i filtrowanie ksiazek,
- dodawanie, edycje, usuwanie i wyswietlanie uzytkownikow,
- wypozyczanie ksiazek,
- zwracanie wypozyczonych ksiazek,
- wyswietlanie listy wypozyczen.

## Struktura projektu

Projekt sklada sie z dwoch katalogow: backendu oraz frontendu. Przy uruchamianiu przez glowny `docker-compose.yml` wazne jest, aby te dwa katalogi znajdowaly sie obok siebie w jednym katalogu nadrzednym.

Nazwa katalogu nadrzednego moze byc dowolna. Istotne sa nazwy i wzajemne polozenie katalogow `Library_Spring` oraz `Library_Spring-frontend`.

```text
dowolna_nazwa
|-- Library_Spring
|   |-- src/main/java/...          # kod backendu Spring Boot
|   |-- src/main/resources         # konfiguracja aplikacji
|   |-- src/test/java/...          # testy integracyjne endpointow
|   |-- Dockerfile                 # obraz Docker dla backendu
|   |-- docker-compose.yml         # backend + PostgreSQL + frontend
|   |-- pom.xml                    # konfiguracja Maven
|   |-- README.md                  # instrukcja projektu
|
|-- Library_Spring-frontend
|   |-- index.html                 # struktura strony
|   |-- styles.css                 # style frontendu
|   |-- app.js                     # komunikacja z REST API
|   |-- Dockerfile                 # obraz Docker dla frontendu
|   |-- nginx.conf                 # konfiguracja Nginx
|   |-- docker-compose.yml         # samodzielne uruchomienie frontendu
```

Glowny plik `Library_Spring/docker-compose.yml` buduje frontend ze sciezki:

```yaml
context: ../Library_Spring-frontend
```

Oznacza to, ze nie trzeba osobno budowac obrazu frontendu. Wystarczy uruchomic Docker Compose z katalogu `Library_Spring`, a Compose sam zbuduje backend, frontend oraz uruchomi baze PostgreSQL.

Jezeli nazwa katalogu frontendu zostanie zmieniona, trzeba odpowiednio poprawic sciezke `context` w `docker-compose.yml`.

## Endpointy API

Domyslny adres backendu:

```text
http://localhost:8080
```

Dokumentacja Swagger UI jest dostepna pod adresem:

```text
http://localhost:8080/swagger-ui/index.html
```

Najwazniejsze endpointy:

```text
GET    /libraries
POST   /libraries
GET    /libraries/{id}
PUT    /libraries/{id}
DELETE /libraries/{id}

GET    /books
POST   /books
GET    /books/{id}
PUT    /books/{id}
DELETE /books/{id}
POST   /books/filter

GET    /users
POST   /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}

GET    /borrows
POST   /borrows?userId={id}&bookId={id}
GET    /borrows/{id}
PUT    /borrows/{id}/return
```

## Uruchomienie calego projektu przez Docker

Najprostszy sposob uruchomienia projektu to Docker Compose. Komenda uruchamia:

- baze danych PostgreSQL,
- backend Spring Boot,
- frontend testowy na Nginx.

W katalogu backendu wykonaj:

```powershell
docker compose up --build -d
```

Po uruchomieniu uslugi beda dostepne pod adresami:

```text
Frontend:   http://localhost:5500
Backend:    http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html
PostgreSQL: localhost:5438
```

## Kontrola kontenerow

Sprawdzenie statusu:

```powershell
docker compose ps
```

Docker Compose sam nadaje nazwy kontenerom na podstawie nazwy projektu i nazw serwisow, np. `library_spring-backend-1`. Dzieki temu latwiej uruchamiac i zatrzymywac projekt bez konfliktow z innymi kontenerami.

Podglad logow:

```powershell
docker compose logs -f
```

Podglad logow tylko backendu:

```powershell
docker compose logs -f backend
```

Zatrzymanie calego srodowiska:

```powershell
docker compose down
```

Zatrzymanie i usuniecie danych bazy:

```powershell
docker compose down -v
```

## Dane bazy PostgreSQL

Domyslna konfiguracja bazy w Docker Compose:

```text
Host lokalny: localhost
Port lokalny: 5438
Baza danych:  library_db
Uzytkownik:   library_user
Haslo:        library_pass
```

W kontenerach backend laczy sie z baza po adresie:

```text
jdbc:postgresql://postgres:5432/library_db
```

Na komputerze hosta baza jest dostepna pod:

```text
jdbc:postgresql://localhost:5438/library_db
```

## Uruchomienie backendu lokalnie bez Dockera

Jezeli baza PostgreSQL jest juz uruchomiona na `localhost:5438`, backend mozna odpalic lokalnie:

```powershell
.\mvnw.cmd spring-boot:run
```

Domyslne ustawienia w `application.properties`:

```text
spring.datasource.url=jdbc:postgresql://localhost:5438/library_db
spring.datasource.username=library_user
spring.datasource.password=library_pass
```

## Uruchomienie testow

Testy korzystaja z bazy H2 w profilu testowym, wiec nie wymagaja dzialajacego PostgreSQL.

```powershell
.\mvnw.cmd test
```

Testy sprawdzaja m.in. endpointy dla ksiazek, bibliotek, uzytkownikow oraz wypozyczen.

## Przykladowe uzycie API

Dodanie biblioteki:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/libraries `
  -ContentType "application/json" `
  -Body '{"name":"Biblioteka Glowna"}'
```

Dodanie ksiazki:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/books `
  -ContentType "application/json" `
  -Body '{"title":"Clean Code","author":"Robert C. Martin","year":2008,"library":{"id":1}}'
```

## Frontend

Frontend jest dostepny po uruchomieniu Docker Compose pod adresem:

```text
http://localhost:5500
```

Na gorze strony znajduje sie pole `API`. Domyslnie powinno wskazywac:

```text
http://localhost:8080
```

Po kliknieciu `Polacz` frontend pobiera dane z backendu i pozwala testowac operacje z poziomu przegladarki.

## Uwagi dla sprawdzajacego

Projekt mozna uruchomic jedna komenda przez Docker Compose. Backend sam tworzy tabele w PostgreSQL dzieki ustawieniu:

```text
spring.jpa.hibernate.ddl-auto=update
```

Dane bazy sa przechowywane w wolumenie Docker `library_postgres_data`. Jezeli potrzebny jest czysty start, nalezy uzyc:

```powershell
docker compose down -v
docker compose up --build -d
```
