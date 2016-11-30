# Rutracker-Stats

Данный курсовой проект выполнен в области обработки больших объемов информации

## Предметная область
Анализ статистики популярного российского торрент-трекера RuTracker.org (зеркало - maintracker.org)

## Задачи анализа
1. Текущая статистика: статистика кол-ва раздач в основых разделах форума (Зарубежное кино, Музыка, ПО и т.д.) +
2. Текущая статистика: статистика соотношения "живых" раздач к "мертвым" в основых разделах форума (Зарубежное кино, Музыка, ПО и т.д.) +
3. Текущая статистика: статистика кол-ва раздающих пользователей в раздачах на форуме ( 0, < 10, <100, <500, >500) +
4. Текущая статистика: среднее кол-во раздающих пользователей в темах основых разделов форума (Зарубежное кино, Музыка, ПО и т.д.) +
5. Средний размер раздач в основых разделах форума (Зарубежное кино, Музыка, ПО и т.д.) +
6. Мониторинг: мониторинг изменения кол-ва раздающих пользователей в основых разделах форума в течение дня (каждый час).
7. Мониторинг: мониторинг изменения кол-ва раздающих пользователей в основых разделах форума в течение недели (каждый день в 20:00).

## Набор данных
Предполагается накопить порядка 200Мб данных статистики, используя API рутрекера - http://api.rutracker.org/v1/docs/ 

## Архитектура системы
Архитектура проекта будет реализована с использованием технологии <b>ELK Stack</b> - Elasticsearch, Logstash и Kibana.

1. Сборка, фильтрации и последующее перенаправление в конечное хранилище данных будет происходить в модуле DataLoader (с ипользованием <b>Logstash</b>)
2. Все данные будут храниться в <b>MongoDB</b>
3. Анализ данных и поиск по базе будет реализован средствами <b>Elasticsearch</b>
4. Визуализация статистики будет реализована в <b>Kibana</b> в виде web-интерфейса.
