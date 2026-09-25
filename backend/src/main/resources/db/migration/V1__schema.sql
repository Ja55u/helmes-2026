CREATE TABLE sector (
    id         INTEGER      PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    parent_id  INTEGER      REFERENCES sector (id),
    sort_order INTEGER      NOT NULL UNIQUE
);

CREATE TABLE submission (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    agree_to_terms BOOLEAN      NOT NULL
);

CREATE TABLE submission_sector (
    submission_id BIGINT  NOT NULL REFERENCES submission (id) ON DELETE CASCADE,
    sector_id     INTEGER NOT NULL REFERENCES sector (id),
    PRIMARY KEY (submission_id, sector_id)
);
