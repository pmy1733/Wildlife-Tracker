CREATE DATABASE wildlife_tracker;
\c wildlife_tracker;

CREATE TABLE IF NOT EXISTS animals (
    id SERIAL PRIMARY KEY,
	name VARCHAR,
	health VARCHAR,
	age VARCHAR
);

CREATE TABLE IF NOT EXISTS sightings (
	id SERIAL PRIMARY KEY,
    animal_id INTEGER,
    ranger VARCHAR,
    age VARCHAR,
    health VARCHAR,
    location VARCHAR,
    FOREIGN KEY(animal_id) REFERENCES public.animals(id)
);

CREATE DATABASE wildlife_tracker_test WITH TEMPLATE wildlife_tracker;
