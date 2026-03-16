CREATE TABLE index_infos
(
  id                   SERIAL PRIMARY KEY,
  created_at           timestamptz           NOT NULL,
  updated_at           timestamptz           NOT NULL,
  index_classification VARCHAR(240)          NOT NULL,
  index_name           VARCHAR(240)          NOT NULL,
  employed_items_count int                   NOT NULL,
  base_point_in_time   date                  NOT NULL,
  base_index           DOUBLE PRECISION      NOT NULL,
  source_type          VARCHAR(10)           NOT NULL CHECK (source_type IN ('USER', 'OPEN_API')),
  favorite             BOOLEAN DEFAULT FALSE NOT NULL,
  is_deleted           VARCHAR(10)           NOT NULL CHECK (is_deleted in ('ACTIVE', 'DELETED'))
);

CREATE INDEX idx_index_infos ON index_infos (index_classification, index_name);

CREATE TABLE index_datas
(
  id                  SERIAL PRIMARY KEY,
  index_info_id       int              NOT NULL,
  base_date           date             NOT NULL,
  source_type         VARCHAR(10)      NOT NULL CHECK (source_type IN ('USER', 'OPEN_API')),
  market_price        DOUBLE PRECISION NOT NULL,
  closing_price       DOUBLE PRECISION NOT NULL,
  high_price          DOUBLE PRECISION NOT NULL,
  low_price           DOUBLE PRECISION NOT NULL,
  versus              DOUBLE PRECISION NOT NULL,
  fluctuation_rate    DOUBLE PRECISION NOT NULL,
  trading_quantity    bigint           NOT NULL,
  trading_price       bigint           NOT NULL,
  market_total_amount bigint           NOT NULL,
  created_at          timestamptz      NOT NULL,
  updated_at          timestamptz      NOT NULL,
  is_deleted          VARCHAR(10)      NOT NULL CHECK (is_deleted in ('ACTIVE', 'DELETED'))
);

CREATE INDEX idx_index_datas ON index_datas (index_info_id, base_date);

CREATE TABLE sync_jobs
(
  id            SERIAL PRIMARY KEY,
  job_type      VARCHAR(10) NOT NULL CHECK (job_type IN ('INDEX_INFO', 'INDEX_DATA')),
  index_info_id int         NOT NULL,
  target_date   date        NOT NULL,
  worker        VARCHAR(15) NOT NULL,
  job_time      timestamptz NOT NULL,
  result        VARCHAR(10) NOT NULL CHECK (result IN ('SUCCESS', 'FAIL'))
);

CREATE TABLE auto_sync_configs
(
  id            SERIAL PRIMARY KEY,
  index_info_id int         NOT NULL,
  enabled       BOOLEAN DEFAULT FALSE,
  updated_at    timestamptz NOT NULL,
  created_at    timestamptz NOT NULL
);

ALTER TABLE index_datas
  ADD CONSTRAINT fk_index_datas_index_info_id
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id);

ALTER TABLE sync_jobs
  ADD CONSTRAINT fk_sync_jobs_index_info_id
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id);

ALTER TABLE auto_sync_configs
  ADD CONSTRAINT fk_auto_sync_configs_index_info_id
    FOREIGN KEY (index_info_id) REFERENCES index_infos (id),
  ADD CONSTRAINT uk_auto_sync_configs_index_info_id UNIQUE (index_info_id);
