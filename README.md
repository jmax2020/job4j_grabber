Проект "Агрегатор вакансий"

Автор: Щербаков М.В.

Функции:
1. Система запускается по расписанию - раз в минуту.  Период запуска указывается в настройках - app.properties. 
2. Работа поводится с базовым сайтом career.habr.com. Работа с разделом  https://career.habr.com/vacancies/java_developer.  Программа считывает все вакансии c первых 5 страниц относящиеся к Java и записывает их в базу.

В проект можно добавить новые сайты без изменения кода.

В проекте можно сделать параллельный парсинг сайтов.