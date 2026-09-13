--
-- PostgreSQL database dump
--

\restrict juRwNTplYuQfdHGSVb5vz7s1HdwiKd6fqzXGw0u9f3kiD6JEnNPsgmr50cT12sk

-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

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

--
-- Data for Name: setting; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.setting VALUES (1, 'Y', 'TRAVEL', 'SYSTEM', '旅遊，用來轉換商品種類 TRAVEL 。', '旅遊', 1, 'TTRAVEL', 'PRODUCT_TYPE', '旅遊');
INSERT INTO public.setting VALUES (2, 'Y', 'CROSS_STRAIT_TRAVEL', 'PRODUCT_TYPE', '兩岸旅遊', '兩岸旅遊', 1, 'TTRAVEL', 'TRAVEL', '兩岸旅遊');
INSERT INTO public.setting VALUES (3, 'Y', 'OVERSEA_TRAVEL', 'PRODUCT_TYPE', '海外旅遊', '海外旅遊', 2, 'TTRAVEL', 'TRAVEL', '海外旅遊');
INSERT INTO public.setting VALUES (4, 'Y', 'DOMESTIC_TRAVEL', 'PRODUCT_TYPE', '國內旅遊', '國內旅遊', 3, 'TTRAVEL', 'TRAVEL', '國內旅遊');
INSERT INTO public.setting VALUES (5, 'Y', 'AIRPORT_PICKUP', 'SYSTEM', '機場接送服務', '機場接送服務', 2, 'TTRAVEL', 'PRODUCT_TYPE', '機場接送服務');
INSERT INTO public.setting VALUES (7, 'Y', 'PICK_UP', 'PRODUCT_TYPE', '機場接送回程（從機場到市區）', '機場接送 (回程)', 2, 'TTRAVEL', 'AIRPORT_PICKUP', '機場接送 (回程)');
INSERT INTO public.setting VALUES (6, 'Y', 'DROP_OFF', 'PRODUCT_TYPE', '機場接送去程（從市區到機場）', '機場接送(去程)', 1, 'TTRAVEL', 'AIRPORT_PICKUP', '機場接送(去程)');


--
-- Name: setting_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.setting_id_seq', 7, true);


--
-- PostgreSQL database dump complete
--

\unrestrict juRwNTplYuQfdHGSVb5vz7s1HdwiKd6fqzXGw0u9f3kiD6JEnNPsgmr50cT12sk

