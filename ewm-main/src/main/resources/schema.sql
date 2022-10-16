 DROP TABLE IF EXISTS participation, compilation_event, compilation, events, locations, category, users, statuses, states;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(200) NOT NULL,
    user_email VARCHAR(200) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS category
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    CONSTRAINT unique_locations UNIQUE (latitude, longitude)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_title VARCHAR(200) NOT NULL,
    event_annotation VARCHAR(1000) NOT NULL,
    event_description VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL,
    publish TIMESTAMP,
    event_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participation_limit INTEGER NOT NULL DEFAULT 0,
    moderation BOOLEAN NOT NULL DEFAULT TRUE,
    state_name VARCHAR(50) NOT NULL,
    confirmed INTEGER NOT NULL,
    CONSTRAINT events_user_id_fk
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT events_category_id_fk
        FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
    CONSTRAINT events_location_id_fk
        FOREIGN KEY (location_id) REFERENCES locations (location_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation
(
    participation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    CONSTRAINT participation_user_id_fk
            FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT participation_event_id_fk
            FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilation
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    compilation_name VARCHAR(200) NOT NULL UNIQUE,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT unique_compilation_event UNIQUE (compilation_id, event_id),
    CONSTRAINT compilation_id_event_id_fk
        FOREIGN KEY (compilation_id) REFERENCES compilation (compilation_id) ON DELETE CASCADE,
    CONSTRAINT event_id_compilation_id_fk
        FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);