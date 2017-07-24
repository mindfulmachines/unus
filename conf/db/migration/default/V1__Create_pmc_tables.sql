
CREATE TABLE "Patient"(
  "enterpriseId" text NOT NULL primary key,
  last text,
  first text,
  middle text,
  suffix text,
  dob text,
  gender text,
  ssn text,
  address1 text,
  address2 text,
  zip text,
  "mothersMaidenName" text,
  mrn text,
  city text,
  state text,
  phone text,
  phone2 text,
  email text,
  alias text
);

CREATE TABLE "BlockedRow"(
  blocker text,
  id1 text,
  id2 text,
  ts TIMESTAMP,
  PRIMARY KEY (blocker, id1, id2)
);


CREATE TABLE "Blocker"(
  blocker text,
  rows bigint,
  ts TIMESTAMP,
  PRIMARY KEY (blocker)
);

CREATE TABLE "Label"(
  batch text,
  id1 text,
  id2 text,
  valid BOOLEAN,
  ts TIMESTAMP,
  PRIMARY KEY (batch, id1, id2)
);

CREATE TABLE "Model"(
  model text,
  ts TIMESTAMP,
  features text,
  blockers text[],
  batches text[],
  trees Int,
  depth Int,
  f1 float,
  precision float,
  recall float,
  PRIMARY KEY (model)
);

CREATE TABLE "Prediction"(
  model text,
  id1 text,
  id2 text,
  label float,
  prediction float,
  ts TIMESTAMP,
  PRIMARY KEY (model, id1, id2)
);

CREATE TABLE "Submission"(
  submission text,
  model text,
  blockers text[],
  f1 float,
  precision float,
  recall float,
  PRIMARY KEY (submission)
);


