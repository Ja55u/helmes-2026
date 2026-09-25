--
-- PostgreSQL database dump
--

\restrict SJW8hQK7ojo9nxLKwVBotcfKwwo4PDUFUzUIYYngLOxANZ0yhxzdJhz6tA6DyIR

-- Dumped from database version 16.15
-- Dumped by pg_dump version 16.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: sector; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sector (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    parent_id integer,
    sort_order integer NOT NULL
);


--
-- Name: submission; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.submission (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    agree_to_terms boolean NOT NULL
);


--
-- Name: submission_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.submission ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.submission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: submission_sector; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.submission_sector (
    submission_id bigint NOT NULL,
    sector_id integer NOT NULL
);


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	schema	SQL	V1__schema.sql	-1807400525	helmes	2026-09-24 06:18:06.992572	37	t
2	2	seed sectors	SQL	V2__seed_sectors.sql	715230302	helmes	2026-09-24 06:18:07.058892	9	t
\.


--
-- Data for Name: sector; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.sector (id, name, parent_id, sort_order) FROM stdin;
1	Manufacturing	\N	1
19	Construction materials	1	2
18	Electronics and Optics	1	3
6	Food and Beverage	1	4
342	Bakery & confectionery products	6	5
43	Beverages	6	6
42	Fish & fish products	6	7
40	Meat & meat products	6	8
39	Milk & dairy products	6	9
437	Other	6	10
378	Sweets & snack food	6	11
13	Furniture	1	12
389	Bathroom/sauna	13	13
385	Bedroom	13	14
390	Children’s room	13	15
98	Kitchen	13	16
101	Living room	13	17
392	Office	13	18
394	Other (Furniture)	13	19
341	Outdoor	13	20
99	Project furniture	13	21
12	Machinery	1	22
94	Machinery components	12	23
91	Machinery equipment/tools	12	24
224	Manufacture of machinery	12	25
97	Maritime	12	26
271	Aluminium and steel workboats	97	27
269	Boat/Yacht building	97	28
230	Ship repair and conversion	97	29
93	Metal structures	12	30
508	Other	12	31
227	Repair and maintenance service	12	32
11	Metalworking	1	33
67	Construction of metal structures	11	34
263	Houses and buildings	11	35
267	Metal products	11	36
542	Metal works	11	37
75	CNC-machining	542	38
62	Forgings, Fasteners	542	39
69	Gas, Plasma, Laser cutting	542	40
66	MIG, TIG, Aluminum welding	542	41
9	Plastic and Rubber	1	42
54	Packaging	9	43
556	Plastic goods	9	44
559	Plastic processing technology	9	45
55	Blowing	559	46
57	Moulding	559	47
53	Plastics welding and processing	559	48
560	Plastic profiles	9	49
5	Printing	1	50
148	Advertising	5	51
150	Book/Periodicals printing	5	52
145	Labelling and packaging printing	5	53
7	Textile and Clothing	1	54
44	Clothing	7	55
45	Textile	7	56
8	Wood	1	57
337	Other (Wood)	8	58
51	Wooden building materials	8	59
47	Wooden houses	8	60
3	Other	\N	61
37	Creative industries	3	62
29	Energy technology	3	63
33	Environment	3	64
2	Service	\N	65
25	Business services	2	66
35	Engineering	2	67
28	Information Technology and Telecommunications	2	68
581	Data processing, Web portals, E-marketing	28	69
576	Programming, Consultancy	28	70
121	Software, Hardware	28	71
122	Telecommunications	28	72
22	Tourism	2	73
141	Translation services	2	74
21	Transport and Logistics	2	75
111	Air	21	76
114	Rail	21	77
112	Road	21	78
113	Water	21	79
\.


--
-- Data for Name: submission; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.submission (id, name, agree_to_terms) FROM stdin;
1	Alice Smith	t
2	Bob	t
\.


--
-- Data for Name: submission_sector; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.submission_sector (submission_id, sector_id) FROM stdin;
1	75
2	2
\.


--
-- Name: submission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.submission_id_seq', 2, true);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: sector sector_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_pkey PRIMARY KEY (id);


--
-- Name: sector sector_sort_order_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_sort_order_key UNIQUE (sort_order);


--
-- Name: submission submission_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission
    ADD CONSTRAINT submission_pkey PRIMARY KEY (id);


--
-- Name: submission_sector submission_sector_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_sector
    ADD CONSTRAINT submission_sector_pkey PRIMARY KEY (submission_id, sector_id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: sector sector_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.sector(id);


--
-- Name: submission_sector submission_sector_sector_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_sector
    ADD CONSTRAINT submission_sector_sector_id_fkey FOREIGN KEY (sector_id) REFERENCES public.sector(id);


--
-- Name: submission_sector submission_sector_submission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.submission_sector
    ADD CONSTRAINT submission_sector_submission_id_fkey FOREIGN KEY (submission_id) REFERENCES public.submission(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict SJW8hQK7ojo9nxLKwVBotcfKwwo4PDUFUzUIYYngLOxANZ0yhxzdJhz6tA6DyIR

