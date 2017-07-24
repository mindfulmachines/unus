ALTER TABLE "Label" DROP CONSTRAINT "Label_pkey";
ALTER TABLE "Label" ADD PRIMARY KEY (batch, id1, id2, ts);
