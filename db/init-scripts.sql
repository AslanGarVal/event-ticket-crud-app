CREATE EXTENSION hstore;
CREATE SCHEMA event_system;

CREATE TABLE IF NOT EXISTS event_system."Events" (
"id" BIGSERIAL NOT NULL PRIMARY KEY,
"event_name" VARCHAR NOT NULL,
"start_date" DATE NOT NULL,
"end_date" DATE NOT NULL,
"available_tickets" INTEGER NOT NULL,
"sold_tickets" INTEGER NOT NULL,
"exchanged_tickets" INTEGER NOT NULL);

CREATE TABLE IF NOT EXISTS event_system."Tickets" (
"event_id" BIGSERIAL NOT NULL,
"id" BIGSERIAL NOT NULL PRIMARY KEY,
"event_ticket_number" INTEGER NOT NULL,
"sold" VARCHAR NOT NULL
)
