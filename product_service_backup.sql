--
-- PostgreSQL database dump
--

\restrict jEfl4vzywgbcTtldDFU1GtTecGeFXVmHQJfIEVXZAcjuTkTrcMSvxGGTrPuAShu

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ab_permission; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_permission (
    id integer NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE public.ab_permission OWNER TO postgres;

--
-- Name: ab_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_permission_id_seq OWNER TO postgres;

--
-- Name: ab_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_permission_id_seq OWNED BY public.ab_permission.id;


--
-- Name: ab_permission_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_permission_view (
    id integer NOT NULL,
    permission_id integer,
    view_menu_id integer
);


ALTER TABLE public.ab_permission_view OWNER TO postgres;

--
-- Name: ab_permission_view_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_permission_view_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_permission_view_id_seq OWNER TO postgres;

--
-- Name: ab_permission_view_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_permission_view_id_seq OWNED BY public.ab_permission_view.id;


--
-- Name: ab_permission_view_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_permission_view_role (
    id integer NOT NULL,
    permission_view_id integer,
    role_id integer
);


ALTER TABLE public.ab_permission_view_role OWNER TO postgres;

--
-- Name: ab_permission_view_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_permission_view_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_permission_view_role_id_seq OWNER TO postgres;

--
-- Name: ab_permission_view_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_permission_view_role_id_seq OWNED BY public.ab_permission_view_role.id;


--
-- Name: ab_register_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_register_user (
    id integer NOT NULL,
    first_name character varying(256) NOT NULL,
    last_name character varying(256) NOT NULL,
    username character varying(512) NOT NULL,
    password character varying(256),
    email character varying(512) NOT NULL,
    registration_date timestamp without time zone,
    registration_hash character varying(256)
);


ALTER TABLE public.ab_register_user OWNER TO postgres;

--
-- Name: ab_register_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_register_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_register_user_id_seq OWNER TO postgres;

--
-- Name: ab_register_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_register_user_id_seq OWNED BY public.ab_register_user.id;


--
-- Name: ab_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_role (
    id integer NOT NULL,
    name character varying(64) NOT NULL
);


ALTER TABLE public.ab_role OWNER TO postgres;

--
-- Name: ab_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_role_id_seq OWNER TO postgres;

--
-- Name: ab_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_role_id_seq OWNED BY public.ab_role.id;


--
-- Name: ab_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_user (
    id integer NOT NULL,
    first_name character varying(256) NOT NULL,
    last_name character varying(256) NOT NULL,
    username character varying(512) NOT NULL,
    password character varying(256),
    active boolean,
    email character varying(512) NOT NULL,
    last_login timestamp without time zone,
    login_count integer,
    fail_login_count integer,
    created_on timestamp without time zone,
    changed_on timestamp without time zone,
    created_by_fk integer,
    changed_by_fk integer
);


ALTER TABLE public.ab_user OWNER TO postgres;

--
-- Name: ab_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_user_id_seq OWNER TO postgres;

--
-- Name: ab_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_user_id_seq OWNED BY public.ab_user.id;


--
-- Name: ab_user_role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_user_role (
    id integer NOT NULL,
    user_id integer,
    role_id integer
);


ALTER TABLE public.ab_user_role OWNER TO postgres;

--
-- Name: ab_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_user_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_user_role_id_seq OWNER TO postgres;

--
-- Name: ab_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_user_role_id_seq OWNED BY public.ab_user_role.id;


--
-- Name: ab_view_menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ab_view_menu (
    id integer NOT NULL,
    name character varying(250) NOT NULL
);


ALTER TABLE public.ab_view_menu OWNER TO postgres;

--
-- Name: ab_view_menu_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.ab_view_menu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ab_view_menu_id_seq OWNER TO postgres;

--
-- Name: ab_view_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.ab_view_menu_id_seq OWNED BY public.ab_view_menu.id;


--
-- Name: alembic_version; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.alembic_version (
    version_num character varying(32) NOT NULL
);


ALTER TABLE public.alembic_version OWNER TO postgres;

--
-- Name: association_value_entry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.association_value_entry (
    id bigint NOT NULL,
    association_key character varying(255) NOT NULL,
    association_value character varying(255),
    saga_id character varying(255) NOT NULL,
    saga_type character varying(255)
);


ALTER TABLE public.association_value_entry OWNER TO postgres;

--
-- Name: association_value_entry_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.association_value_entry_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.association_value_entry_seq OWNER TO postgres;

--
-- Name: callback_request; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.callback_request (
    id integer NOT NULL,
    created_at timestamp with time zone NOT NULL,
    priority_weight integer NOT NULL,
    callback_data json NOT NULL,
    callback_type character varying(20) NOT NULL,
    processor_subdir character varying(2000)
);


ALTER TABLE public.callback_request OWNER TO postgres;

--
-- Name: callback_request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.callback_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.callback_request_id_seq OWNER TO postgres;

--
-- Name: callback_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.callback_request_id_seq OWNED BY public.callback_request.id;


--
-- Name: connection; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.connection (
    id integer NOT NULL,
    conn_id character varying(250) NOT NULL,
    conn_type character varying(500) NOT NULL,
    description text,
    host character varying(500),
    schema character varying(500),
    login text,
    password text,
    port integer,
    is_encrypted boolean,
    is_extra_encrypted boolean,
    extra text
);


ALTER TABLE public.connection OWNER TO postgres;

--
-- Name: connection_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.connection_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.connection_id_seq OWNER TO postgres;

--
-- Name: connection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.connection_id_seq OWNED BY public.connection.id;


--
-- Name: dag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag (
    dag_id character varying(250) NOT NULL,
    root_dag_id character varying(250),
    is_paused boolean,
    is_subdag boolean,
    is_active boolean,
    last_parsed_time timestamp with time zone,
    last_pickled timestamp with time zone,
    last_expired timestamp with time zone,
    scheduler_lock boolean,
    pickle_id integer,
    fileloc character varying(2000),
    processor_subdir character varying(2000),
    owners character varying(2000),
    dag_display_name character varying(2000),
    description text,
    default_view character varying(25),
    schedule_interval text,
    timetable_description character varying(1000),
    dataset_expression json,
    max_active_tasks integer NOT NULL,
    max_active_runs integer,
    max_consecutive_failed_dag_runs integer NOT NULL,
    has_task_concurrency_limits boolean NOT NULL,
    has_import_errors boolean DEFAULT false,
    next_dagrun timestamp with time zone,
    next_dagrun_data_interval_start timestamp with time zone,
    next_dagrun_data_interval_end timestamp with time zone,
    next_dagrun_create_after timestamp with time zone
);


ALTER TABLE public.dag OWNER TO postgres;

--
-- Name: dag_code; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_code (
    fileloc_hash bigint NOT NULL,
    fileloc character varying(2000) NOT NULL,
    last_updated timestamp with time zone NOT NULL,
    source_code text NOT NULL
);


ALTER TABLE public.dag_code OWNER TO postgres;

--
-- Name: dag_owner_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_owner_attributes (
    dag_id character varying(250) NOT NULL,
    owner character varying(500) NOT NULL,
    link character varying(500) NOT NULL
);


ALTER TABLE public.dag_owner_attributes OWNER TO postgres;

--
-- Name: dag_pickle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_pickle (
    id integer NOT NULL,
    pickle bytea,
    created_dttm timestamp with time zone,
    pickle_hash bigint
);


ALTER TABLE public.dag_pickle OWNER TO postgres;

--
-- Name: dag_pickle_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dag_pickle_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dag_pickle_id_seq OWNER TO postgres;

--
-- Name: dag_pickle_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dag_pickle_id_seq OWNED BY public.dag_pickle.id;


--
-- Name: dag_run; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_run (
    id integer NOT NULL,
    dag_id character varying(250) NOT NULL,
    queued_at timestamp with time zone,
    execution_date timestamp with time zone NOT NULL,
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    state character varying(50),
    run_id character varying(250) NOT NULL,
    creating_job_id integer,
    external_trigger boolean,
    run_type character varying(50) NOT NULL,
    conf bytea,
    data_interval_start timestamp with time zone,
    data_interval_end timestamp with time zone,
    last_scheduling_decision timestamp with time zone,
    dag_hash character varying(32),
    log_template_id integer,
    updated_at timestamp with time zone,
    clear_number integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.dag_run OWNER TO postgres;

--
-- Name: dag_run_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dag_run_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dag_run_id_seq OWNER TO postgres;

--
-- Name: dag_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dag_run_id_seq OWNED BY public.dag_run.id;


--
-- Name: dag_run_note; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_run_note (
    user_id integer,
    dag_run_id integer NOT NULL,
    content character varying(1000),
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.dag_run_note OWNER TO postgres;

--
-- Name: dag_schedule_dataset_reference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_schedule_dataset_reference (
    dataset_id integer NOT NULL,
    dag_id character varying(250) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.dag_schedule_dataset_reference OWNER TO postgres;

--
-- Name: dag_tag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_tag (
    name character varying(100) NOT NULL,
    dag_id character varying(250) NOT NULL
);


ALTER TABLE public.dag_tag OWNER TO postgres;

--
-- Name: dag_warning; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dag_warning (
    dag_id character varying(250) NOT NULL,
    warning_type character varying(50) NOT NULL,
    message text NOT NULL,
    "timestamp" timestamp with time zone NOT NULL
);


ALTER TABLE public.dag_warning OWNER TO postgres;

--
-- Name: dagrun_dataset_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dagrun_dataset_event (
    dag_run_id integer NOT NULL,
    event_id integer NOT NULL
);


ALTER TABLE public.dagrun_dataset_event OWNER TO postgres;

--
-- Name: dataset; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dataset (
    id integer NOT NULL,
    uri character varying(3000) NOT NULL,
    extra json NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    is_orphaned boolean DEFAULT false NOT NULL
);


ALTER TABLE public.dataset OWNER TO postgres;

--
-- Name: dataset_dag_run_queue; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dataset_dag_run_queue (
    dataset_id integer NOT NULL,
    target_dag_id character varying(250) NOT NULL,
    created_at timestamp with time zone NOT NULL
);


ALTER TABLE public.dataset_dag_run_queue OWNER TO postgres;

--
-- Name: dataset_event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dataset_event (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    extra json NOT NULL,
    source_task_id character varying(250),
    source_dag_id character varying(250),
    source_run_id character varying(250),
    source_map_index integer DEFAULT '-1'::integer,
    "timestamp" timestamp with time zone NOT NULL
);


ALTER TABLE public.dataset_event OWNER TO postgres;

--
-- Name: dataset_event_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dataset_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_event_id_seq OWNER TO postgres;

--
-- Name: dataset_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dataset_event_id_seq OWNED BY public.dataset_event.id;


--
-- Name: dataset_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dataset_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_id_seq OWNER TO postgres;

--
-- Name: dataset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dataset_id_seq OWNED BY public.dataset.id;


--
-- Name: dead_letter_entry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dead_letter_entry (
    dead_letter_id character varying(255) NOT NULL,
    cause_message character varying(1023),
    cause_type character varying(255),
    diagnostics oid,
    enqueued_at timestamp(6) with time zone NOT NULL,
    last_touched timestamp(6) with time zone,
    aggregate_identifier character varying(255),
    event_identifier character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    sequence_number bigint,
    time_stamp character varying(255) NOT NULL,
    token oid,
    token_type character varying(255),
    type character varying(255),
    processing_group character varying(255) NOT NULL,
    processing_started timestamp(6) with time zone,
    sequence_identifier character varying(255) NOT NULL,
    sequence_index bigint NOT NULL
);


ALTER TABLE public.dead_letter_entry OWNER TO postgres;

--
-- Name: import_error; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.import_error (
    id integer NOT NULL,
    "timestamp" timestamp with time zone,
    filename character varying(1024),
    stacktrace text,
    processor_subdir character varying(2000)
);


ALTER TABLE public.import_error OWNER TO postgres;

--
-- Name: import_error_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.import_error_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.import_error_id_seq OWNER TO postgres;

--
-- Name: import_error_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.import_error_id_seq OWNED BY public.import_error.id;


--
-- Name: job; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.job (
    id integer NOT NULL,
    dag_id character varying(250),
    state character varying(20),
    job_type character varying(30),
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    latest_heartbeat timestamp with time zone,
    executor_class character varying(500),
    hostname character varying(500),
    unixname character varying(1000)
);


ALTER TABLE public.job OWNER TO postgres;

--
-- Name: job_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.job_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.job_id_seq OWNER TO postgres;

--
-- Name: job_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.job_id_seq OWNED BY public.job.id;


--
-- Name: log; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.log (
    id integer NOT NULL,
    dttm timestamp with time zone,
    dag_id character varying(250),
    task_id character varying(250),
    map_index integer,
    event character varying(60),
    execution_date timestamp with time zone,
    run_id character varying(250),
    owner character varying(500),
    owner_display_name character varying(500),
    extra text
);


ALTER TABLE public.log OWNER TO postgres;

--
-- Name: log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.log_id_seq OWNER TO postgres;

--
-- Name: log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.log_id_seq OWNED BY public.log.id;


--
-- Name: log_template; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.log_template (
    id integer NOT NULL,
    filename text NOT NULL,
    elasticsearch_id text NOT NULL,
    created_at timestamp with time zone NOT NULL
);


ALTER TABLE public.log_template OWNER TO postgres;

--
-- Name: log_template_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.log_template_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.log_template_id_seq OWNER TO postgres;

--
-- Name: log_template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.log_template_id_seq OWNED BY public.log_template.id;


--
-- Name: order_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_view (
    order_id character varying(255) NOT NULL,
    amount numeric(38,2),
    status character varying(255),
    version bigint
);


ALTER TABLE public.order_view OWNER TO postgres;

--
-- Name: order_view_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_view_items (
    order_id character varying(255) NOT NULL,
    price numeric(38,2),
    product_id character varying(255),
    quantity integer
);


ALTER TABLE public.order_view_items OWNER TO postgres;

--
-- Name: product_images; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_images (
    product_id character varying(64) NOT NULL,
    image_url character varying(255)
);


ALTER TABLE public.product_images OWNER TO postgres;

--
-- Name: product_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_tags (
    product_id character varying(64) NOT NULL,
    tag character varying(255)
);


ALTER TABLE public.product_tags OWNER TO postgres;

--
-- Name: product_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_view (
    product_id character varying(64) NOT NULL,
    description text,
    name character varying(255),
    price numeric(38,2),
    status character varying(20),
    stock integer,
    sub_type character varying(100),
    tenant_id character varying(255),
    type character varying(100),
    version bigint,
    CONSTRAINT product_view_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);


ALTER TABLE public.product_view OWNER TO postgres;

--
-- Name: rendered_task_instance_fields; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rendered_task_instance_fields (
    dag_id character varying(250) NOT NULL,
    task_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer DEFAULT '-1'::integer NOT NULL,
    rendered_fields json NOT NULL,
    k8s_pod_yaml json
);


ALTER TABLE public.rendered_task_instance_fields OWNER TO postgres;

--
-- Name: saga_entry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.saga_entry (
    saga_id character varying(255) NOT NULL,
    revision character varying(255),
    saga_type character varying(255),
    serialized_saga oid
);


ALTER TABLE public.saga_entry OWNER TO postgres;

--
-- Name: serialized_dag; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.serialized_dag (
    dag_id character varying(250) NOT NULL,
    fileloc character varying(2000) NOT NULL,
    fileloc_hash bigint NOT NULL,
    data json,
    data_compressed bytea,
    last_updated timestamp with time zone NOT NULL,
    dag_hash character varying(32) NOT NULL,
    processor_subdir character varying(2000)
);


ALTER TABLE public.serialized_dag OWNER TO postgres;

--
-- Name: session; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.session (
    id integer NOT NULL,
    session_id character varying(255),
    data bytea,
    expiry timestamp without time zone
);


ALTER TABLE public.session OWNER TO postgres;

--
-- Name: session_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.session_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.session_id_seq OWNER TO postgres;

--
-- Name: session_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.session_id_seq OWNED BY public.session.id;


--
-- Name: setting; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.setting (
    id bigint NOT NULL,
    active_flag character varying(255),
    code character varying(255),
    data_type character varying(255),
    description character varying(255),
    name character varying(255),
    priority_no integer,
    tenant_id character varying(255),
    type character varying(255),
    value character varying(255),
    CONSTRAINT setting_active_flag_check CHECK (((active_flag)::text = ANY ((ARRAY['Y'::character varying, 'N'::character varying])::text[])))
);


ALTER TABLE public.setting OWNER TO postgres;

--
-- Name: setting_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.setting ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.setting_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: sla_miss; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sla_miss (
    task_id character varying(250) NOT NULL,
    dag_id character varying(250) NOT NULL,
    execution_date timestamp with time zone NOT NULL,
    email_sent boolean,
    "timestamp" timestamp with time zone,
    description text,
    notification_sent boolean
);


ALTER TABLE public.sla_miss OWNER TO postgres;

--
-- Name: slot_pool; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.slot_pool (
    id integer NOT NULL,
    pool character varying(256),
    slots integer,
    description text,
    include_deferred boolean NOT NULL
);


ALTER TABLE public.slot_pool OWNER TO postgres;

--
-- Name: slot_pool_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.slot_pool_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.slot_pool_id_seq OWNER TO postgres;

--
-- Name: slot_pool_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.slot_pool_id_seq OWNED BY public.slot_pool.id;


--
-- Name: task_fail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_fail (
    id integer NOT NULL,
    task_id character varying(250) NOT NULL,
    dag_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer DEFAULT '-1'::integer NOT NULL,
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    duration integer
);


ALTER TABLE public.task_fail OWNER TO postgres;

--
-- Name: task_fail_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.task_fail_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.task_fail_id_seq OWNER TO postgres;

--
-- Name: task_fail_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.task_fail_id_seq OWNED BY public.task_fail.id;


--
-- Name: task_instance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_instance (
    task_id character varying(250) NOT NULL,
    dag_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer DEFAULT '-1'::integer NOT NULL,
    start_date timestamp with time zone,
    end_date timestamp with time zone,
    duration double precision,
    state character varying(20),
    try_number integer,
    max_tries integer DEFAULT '-1'::integer,
    hostname character varying(1000),
    unixname character varying(1000),
    job_id integer,
    pool character varying(256) NOT NULL,
    pool_slots integer NOT NULL,
    queue character varying(256),
    priority_weight integer,
    operator character varying(1000),
    custom_operator_name character varying(1000),
    queued_dttm timestamp with time zone,
    queued_by_job_id integer,
    pid integer,
    executor_config bytea,
    updated_at timestamp with time zone,
    rendered_map_index character varying(250),
    external_executor_id character varying(250),
    trigger_id integer,
    trigger_timeout timestamp without time zone,
    next_method character varying(1000),
    next_kwargs json,
    task_display_name character varying(2000)
);


ALTER TABLE public.task_instance OWNER TO postgres;

--
-- Name: task_instance_note; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_instance_note (
    user_id integer,
    task_id character varying(250) NOT NULL,
    dag_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer NOT NULL,
    content character varying(1000),
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.task_instance_note OWNER TO postgres;

--
-- Name: task_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_map (
    dag_id character varying(250) NOT NULL,
    task_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer NOT NULL,
    length integer NOT NULL,
    keys json,
    CONSTRAINT ck_task_map_task_map_length_not_negative CHECK ((length >= 0))
);


ALTER TABLE public.task_map OWNER TO postgres;

--
-- Name: task_outlet_dataset_reference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_outlet_dataset_reference (
    dataset_id integer NOT NULL,
    dag_id character varying(250) NOT NULL,
    task_id character varying(250) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);


ALTER TABLE public.task_outlet_dataset_reference OWNER TO postgres;

--
-- Name: task_reschedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_reschedule (
    id integer NOT NULL,
    task_id character varying(250) NOT NULL,
    dag_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    map_index integer DEFAULT '-1'::integer NOT NULL,
    try_number integer NOT NULL,
    start_date timestamp with time zone NOT NULL,
    end_date timestamp with time zone NOT NULL,
    duration integer NOT NULL,
    reschedule_date timestamp with time zone NOT NULL
);


ALTER TABLE public.task_reschedule OWNER TO postgres;

--
-- Name: task_reschedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.task_reschedule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.task_reschedule_id_seq OWNER TO postgres;

--
-- Name: task_reschedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.task_reschedule_id_seq OWNED BY public.task_reschedule.id;


--
-- Name: token_entry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.token_entry (
    processor_name character varying(255) NOT NULL,
    segment integer NOT NULL,
    owner character varying(255),
    "timestamp" character varying(255) NOT NULL,
    token oid,
    token_type character varying(255)
);


ALTER TABLE public.token_entry OWNER TO postgres;

--
-- Name: trigger; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trigger (
    id integer NOT NULL,
    classpath character varying(1000) NOT NULL,
    kwargs text NOT NULL,
    created_date timestamp with time zone NOT NULL,
    triggerer_id integer
);


ALTER TABLE public.trigger OWNER TO postgres;

--
-- Name: trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trigger_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trigger_id_seq OWNER TO postgres;

--
-- Name: trigger_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trigger_id_seq OWNED BY public.trigger.id;


--
-- Name: user_favorite_view; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_favorite_view (
    favorite_id character varying(64) NOT NULL,
    created_at timestamp(6) without time zone,
    product_id character varying(255) NOT NULL,
    tenant_id character varying(255) NOT NULL,
    user_id character varying(255) NOT NULL
);


ALTER TABLE public.user_favorite_view OWNER TO postgres;

--
-- Name: variable; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.variable (
    id integer NOT NULL,
    key character varying(250),
    val text,
    description text,
    is_encrypted boolean
);


ALTER TABLE public.variable OWNER TO postgres;

--
-- Name: variable_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.variable_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.variable_id_seq OWNER TO postgres;

--
-- Name: variable_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.variable_id_seq OWNED BY public.variable.id;


--
-- Name: xcom; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.xcom (
    dag_run_id integer NOT NULL,
    task_id character varying(250) NOT NULL,
    map_index integer DEFAULT '-1'::integer NOT NULL,
    key character varying(512) NOT NULL,
    dag_id character varying(250) NOT NULL,
    run_id character varying(250) NOT NULL,
    value bytea,
    "timestamp" timestamp with time zone NOT NULL
);


ALTER TABLE public.xcom OWNER TO postgres;

--
-- Name: ab_permission id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission ALTER COLUMN id SET DEFAULT nextval('public.ab_permission_id_seq'::regclass);


--
-- Name: ab_permission_view id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view ALTER COLUMN id SET DEFAULT nextval('public.ab_permission_view_id_seq'::regclass);


--
-- Name: ab_permission_view_role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view_role ALTER COLUMN id SET DEFAULT nextval('public.ab_permission_view_role_id_seq'::regclass);


--
-- Name: ab_register_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_register_user ALTER COLUMN id SET DEFAULT nextval('public.ab_register_user_id_seq'::regclass);


--
-- Name: ab_role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_role ALTER COLUMN id SET DEFAULT nextval('public.ab_role_id_seq'::regclass);


--
-- Name: ab_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user ALTER COLUMN id SET DEFAULT nextval('public.ab_user_id_seq'::regclass);


--
-- Name: ab_user_role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user_role ALTER COLUMN id SET DEFAULT nextval('public.ab_user_role_id_seq'::regclass);


--
-- Name: ab_view_menu id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_view_menu ALTER COLUMN id SET DEFAULT nextval('public.ab_view_menu_id_seq'::regclass);


--
-- Name: callback_request id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.callback_request ALTER COLUMN id SET DEFAULT nextval('public.callback_request_id_seq'::regclass);


--
-- Name: connection id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connection ALTER COLUMN id SET DEFAULT nextval('public.connection_id_seq'::regclass);


--
-- Name: dag_pickle id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_pickle ALTER COLUMN id SET DEFAULT nextval('public.dag_pickle_id_seq'::regclass);


--
-- Name: dag_run id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run ALTER COLUMN id SET DEFAULT nextval('public.dag_run_id_seq'::regclass);


--
-- Name: dataset id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset ALTER COLUMN id SET DEFAULT nextval('public.dataset_id_seq'::regclass);


--
-- Name: dataset_event id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset_event ALTER COLUMN id SET DEFAULT nextval('public.dataset_event_id_seq'::regclass);


--
-- Name: import_error id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import_error ALTER COLUMN id SET DEFAULT nextval('public.import_error_id_seq'::regclass);


--
-- Name: job id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.job ALTER COLUMN id SET DEFAULT nextval('public.job_id_seq'::regclass);


--
-- Name: log id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log ALTER COLUMN id SET DEFAULT nextval('public.log_id_seq'::regclass);


--
-- Name: log_template id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_template ALTER COLUMN id SET DEFAULT nextval('public.log_template_id_seq'::regclass);


--
-- Name: session id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session ALTER COLUMN id SET DEFAULT nextval('public.session_id_seq'::regclass);


--
-- Name: slot_pool id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.slot_pool ALTER COLUMN id SET DEFAULT nextval('public.slot_pool_id_seq'::regclass);


--
-- Name: task_fail id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_fail ALTER COLUMN id SET DEFAULT nextval('public.task_fail_id_seq'::regclass);


--
-- Name: task_reschedule id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_reschedule ALTER COLUMN id SET DEFAULT nextval('public.task_reschedule_id_seq'::regclass);


--
-- Name: trigger id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger ALTER COLUMN id SET DEFAULT nextval('public.trigger_id_seq'::regclass);


--
-- Name: variable id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.variable ALTER COLUMN id SET DEFAULT nextval('public.variable_id_seq'::regclass);


--
-- Name: 24654; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24654');


ALTER LARGE OBJECT 24654 OWNER TO postgres;

--
-- Name: 24714; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24714');


ALTER LARGE OBJECT 24714 OWNER TO postgres;

--
-- Name: 24715; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24715');


ALTER LARGE OBJECT 24715 OWNER TO postgres;

--
-- Name: 24716; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24716');


ALTER LARGE OBJECT 24716 OWNER TO postgres;

--
-- Name: 24717; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24717');


ALTER LARGE OBJECT 24717 OWNER TO postgres;

--
-- Name: 24721; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24721');


ALTER LARGE OBJECT 24721 OWNER TO postgres;

--
-- Name: 24722; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24722');


ALTER LARGE OBJECT 24722 OWNER TO postgres;

--
-- Name: 24723; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24723');


ALTER LARGE OBJECT 24723 OWNER TO postgres;

--
-- Name: 24724; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24724');


ALTER LARGE OBJECT 24724 OWNER TO postgres;

--
-- Name: 24728; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24728');


ALTER LARGE OBJECT 24728 OWNER TO postgres;

--
-- Name: 24729; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24729');


ALTER LARGE OBJECT 24729 OWNER TO postgres;

--
-- Name: 24730; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24730');


ALTER LARGE OBJECT 24730 OWNER TO postgres;

--
-- Name: 24731; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24731');


ALTER LARGE OBJECT 24731 OWNER TO postgres;

--
-- Name: 24732; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24732');


ALTER LARGE OBJECT 24732 OWNER TO postgres;

--
-- Name: 24734; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24734');


ALTER LARGE OBJECT 24734 OWNER TO postgres;

--
-- Name: 24735; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24735');


ALTER LARGE OBJECT 24735 OWNER TO postgres;

--
-- Name: 24737; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24737');


ALTER LARGE OBJECT 24737 OWNER TO postgres;

--
-- Name: 24738; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24738');


ALTER LARGE OBJECT 24738 OWNER TO postgres;

--
-- Name: 24740; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24740');


ALTER LARGE OBJECT 24740 OWNER TO postgres;

--
-- Name: 24742; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24742');


ALTER LARGE OBJECT 24742 OWNER TO postgres;

--
-- Name: 24743; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24743');


ALTER LARGE OBJECT 24743 OWNER TO postgres;

--
-- Name: 24744; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24744');


ALTER LARGE OBJECT 24744 OWNER TO postgres;

--
-- Name: 24745; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24745');


ALTER LARGE OBJECT 24745 OWNER TO postgres;

--
-- Name: 24746; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24746');


ALTER LARGE OBJECT 24746 OWNER TO postgres;

--
-- Name: 24747; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24747');


ALTER LARGE OBJECT 24747 OWNER TO postgres;

--
-- Name: 24760; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24760');


ALTER LARGE OBJECT 24760 OWNER TO postgres;

--
-- Name: 24761; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24761');


ALTER LARGE OBJECT 24761 OWNER TO postgres;

--
-- Name: 24762; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24762');


ALTER LARGE OBJECT 24762 OWNER TO postgres;

--
-- Name: 24763; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24763');


ALTER LARGE OBJECT 24763 OWNER TO postgres;

--
-- Name: 24764; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24764');


ALTER LARGE OBJECT 24764 OWNER TO postgres;

--
-- Name: 24765; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24765');


ALTER LARGE OBJECT 24765 OWNER TO postgres;

--
-- Name: 24766; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24766');


ALTER LARGE OBJECT 24766 OWNER TO postgres;

--
-- Name: 24767; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24767');


ALTER LARGE OBJECT 24767 OWNER TO postgres;

--
-- Name: 24768; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24768');


ALTER LARGE OBJECT 24768 OWNER TO postgres;

--
-- Name: 24769; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24769');


ALTER LARGE OBJECT 24769 OWNER TO postgres;

--
-- Name: 24770; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24770');


ALTER LARGE OBJECT 24770 OWNER TO postgres;

--
-- Name: 24771; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24771');


ALTER LARGE OBJECT 24771 OWNER TO postgres;

--
-- Name: 24772; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24772');


ALTER LARGE OBJECT 24772 OWNER TO postgres;

--
-- Name: 24773; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24773');


ALTER LARGE OBJECT 24773 OWNER TO postgres;

--
-- Name: 24774; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24774');


ALTER LARGE OBJECT 24774 OWNER TO postgres;

--
-- Name: 24775; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24775');


ALTER LARGE OBJECT 24775 OWNER TO postgres;

--
-- Name: 24778; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24778');


ALTER LARGE OBJECT 24778 OWNER TO postgres;

--
-- Name: 24779; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('24779');


ALTER LARGE OBJECT 24779 OWNER TO postgres;

--
-- Data for Name: ab_permission; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_permission (id, name) FROM stdin;
1	can_edit
2	can_read
3	can_create
4	can_delete
5	menu_access
\.


--
-- Data for Name: ab_permission_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_permission_view (id, permission_id, view_menu_id) FROM stdin;
1	1	4
2	2	4
3	1	5
4	2	5
5	1	6
6	2	6
7	3	8
8	2	8
9	1	8
10	4	8
11	5	9
12	5	10
13	3	11
14	2	11
15	1	11
16	4	11
17	5	12
18	2	13
19	5	14
20	2	15
21	5	16
22	2	17
23	5	18
24	2	19
25	5	20
26	3	23
27	2	23
28	1	23
29	4	23
30	5	23
31	5	24
32	2	25
33	5	25
34	2	26
35	5	26
36	3	27
37	2	27
38	1	27
39	4	27
40	5	27
41	5	28
42	3	29
43	2	29
44	1	29
45	4	29
46	5	29
47	2	30
48	5	30
49	2	31
50	5	31
51	2	32
52	5	32
53	3	33
54	2	33
55	1	33
56	4	33
57	5	33
58	2	34
59	5	34
60	4	34
61	1	34
62	2	35
63	5	35
64	2	36
65	5	36
66	3	37
67	2	37
68	1	37
69	4	37
70	5	37
71	2	38
72	4	38
73	5	38
74	5	40
75	5	44
76	5	45
77	5	46
78	5	47
79	5	48
80	1	49
81	2	49
82	4	49
83	1	44
84	4	44
85	2	44
86	2	40
87	2	50
88	2	46
89	2	45
90	2	51
91	2	52
92	2	53
93	2	54
94	3	46
95	4	46
\.


--
-- Data for Name: ab_permission_view_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_permission_view_role (id, permission_view_id, role_id) FROM stdin;
1	1	1
2	2	1
3	3	1
4	4	1
5	5	1
6	6	1
7	7	1
8	8	1
9	9	1
10	10	1
11	11	1
12	12	1
13	13	1
14	14	1
15	15	1
16	16	1
17	17	1
18	18	1
19	19	1
20	20	1
21	21	1
22	22	1
23	23	1
24	24	1
25	25	1
26	26	1
27	27	1
28	28	1
29	29	1
30	30	1
31	31	1
32	32	1
33	33	1
34	34	1
35	35	1
36	36	1
37	37	1
38	38	1
39	39	1
40	40	1
41	41	1
42	42	1
43	43	1
44	44	1
45	45	1
46	46	1
47	47	1
48	48	1
49	49	1
50	50	1
51	51	1
52	52	1
53	53	1
54	54	1
55	55	1
56	56	1
57	57	1
58	58	1
59	59	1
60	60	1
61	61	1
62	62	1
63	63	1
64	64	1
65	65	1
66	66	1
67	67	1
68	68	1
69	69	1
70	70	1
71	71	1
72	72	1
73	73	1
74	74	1
75	75	1
76	76	1
77	77	1
78	78	1
79	79	1
80	85	3
81	86	3
82	87	3
83	27	3
84	88	3
85	89	3
86	67	3
87	90	3
88	91	3
89	32	3
90	4	3
91	3	3
92	6	3
93	5	3
94	58	3
95	43	3
96	92	3
97	71	3
98	93	3
99	31	3
100	75	3
101	74	3
102	30	3
103	77	3
104	76	3
105	78	3
106	79	3
107	33	3
108	59	3
109	46	3
110	85	4
111	86	4
112	87	4
113	27	4
114	88	4
115	89	4
116	67	4
117	90	4
118	91	4
119	32	4
120	4	4
121	3	4
122	6	4
123	5	4
124	58	4
125	43	4
126	92	4
127	71	4
128	93	4
129	31	4
130	75	4
131	74	4
132	30	4
133	77	4
134	76	4
135	78	4
136	79	4
137	33	4
138	59	4
139	46	4
140	83	4
141	84	4
142	42	4
143	44	4
144	45	4
145	26	4
146	28	4
147	29	4
148	94	4
149	85	5
150	86	5
151	87	5
152	27	5
153	88	5
154	89	5
155	67	5
156	90	5
157	91	5
158	32	5
159	4	5
160	3	5
161	6	5
162	5	5
163	58	5
164	43	5
165	92	5
166	71	5
167	93	5
168	31	5
169	75	5
170	74	5
171	30	5
172	77	5
173	76	5
174	78	5
175	79	5
176	33	5
177	59	5
178	46	5
179	83	5
180	84	5
181	42	5
182	44	5
183	45	5
184	26	5
185	28	5
186	29	5
187	94	5
188	51	5
189	41	5
190	52	5
191	57	5
192	70	5
193	63	5
194	40	5
195	65	5
196	73	5
197	53	5
198	54	5
199	55	5
200	56	5
201	66	5
202	68	5
203	69	5
204	62	5
205	64	5
206	36	5
207	37	5
208	38	5
209	39	5
210	72	5
211	95	5
212	85	1
213	86	1
214	87	1
215	88	1
216	89	1
217	90	1
218	91	1
219	92	1
220	93	1
221	83	1
222	84	1
223	94	1
224	95	1
\.


--
-- Data for Name: ab_register_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_register_user (id, first_name, last_name, username, password, email, registration_date, registration_hash) FROM stdin;
\.


--
-- Data for Name: ab_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_role (id, name) FROM stdin;
1	Admin
2	Public
3	Viewer
4	User
5	Op
\.


--
-- Data for Name: ab_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_user (id, first_name, last_name, username, password, active, email, last_login, login_count, fail_login_count, created_on, changed_on, created_by_fk, changed_by_fk) FROM stdin;
1	admin	admin	admin	pbkdf2:sha256:260000$tWlWgzwV7F3ktEXt$6b3707ea7b82980b53e3edc9e25e1e4802bb2d6b3d75af61ba03daa37c8ccd01	t	admin@example.com	\N	\N	\N	2026-09-12 18:28:18.235535	2026-09-12 18:28:18.235542	\N	\N
\.


--
-- Data for Name: ab_user_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_user_role (id, user_id, role_id) FROM stdin;
1	1	1
\.


--
-- Data for Name: ab_view_menu; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ab_view_menu (id, name) FROM stdin;
1	IndexView
2	UtilView
3	LocaleView
4	Passwords
5	My Password
6	My Profile
7	AuthDBView
8	Users
9	List Users
10	Security
11	Roles
12	List Roles
13	User Stats Chart
14	User's Statistics
15	Permissions
16	Actions
17	View Menus
18	Resources
19	Permission Views
20	Permission Pairs
21	AutocompleteView
22	Airflow
23	DAG Runs
24	Browse
25	Jobs
26	Audit Logs
27	Variables
28	Admin
29	Task Instances
30	Task Reschedules
31	Triggers
32	Configurations
33	Connections
34	SLA Misses
35	Plugins
36	Providers
37	Pools
38	XComs
39	DagDependenciesView
40	DAG Dependencies
41	RedocView
42	DevView
43	DocsView
44	DAGs
45	Cluster Activity
46	Datasets
47	Documentation
48	Docs
49	DAG:spark_recommender_batch
50	DAG Code
51	ImportError
52	DAG Warnings
53	Task Logs
54	Website
\.


--
-- Data for Name: alembic_version; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.alembic_version (version_num) FROM stdin;
686269002441
\.


--
-- Data for Name: association_value_entry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.association_value_entry (id, association_key, association_value, saga_id, saga_type) FROM stdin;
1	orderId	16f6b63a-3314-4915-acf0-01b2edcf4975	144cca18-f238-482e-9d14-0bbef403b5c3	com.example.demo.application.saga.OrderManagementSaga
2	paymentId	87f68711-14bc-42bb-8717-c6d95b9430bd	144cca18-f238-482e-9d14-0bbef403b5c3	com.example.demo.application.saga.OrderManagementSaga
\.


--
-- Data for Name: callback_request; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.callback_request (id, created_at, priority_weight, callback_data, callback_type, processor_subdir) FROM stdin;
\.


--
-- Data for Name: connection; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.connection (id, conn_id, conn_type, description, host, schema, login, password, port, is_encrypted, is_extra_encrypted, extra) FROM stdin;
\.


--
-- Data for Name: dag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag (dag_id, root_dag_id, is_paused, is_subdag, is_active, last_parsed_time, last_pickled, last_expired, scheduler_lock, pickle_id, fileloc, processor_subdir, owners, dag_display_name, description, default_view, schedule_interval, timetable_description, dataset_expression, max_active_tasks, max_active_runs, max_consecutive_failed_dag_runs, has_task_concurrency_limits, has_import_errors, next_dagrun, next_dagrun_data_interval_start, next_dagrun_data_interval_end, next_dagrun_create_after) FROM stdin;
spark_recommender_batch	\N	t	f	t	2026-09-13 17:29:15.689908+08	\N	\N	\N	\N	/opt/airflow/dags/recommendation_dag.py	/opt/airflow/dags	omni-store	\N	Run Spark ALS Recommendation Batch Job using Maven	grid	"@daily"	At 00:00	null	16	16	0	f	f	2026-09-12 08:00:00+08	2026-09-12 08:00:00+08	2026-09-13 08:00:00+08	2026-09-13 08:00:00+08
\.


--
-- Data for Name: dag_code; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_code (fileloc_hash, fileloc, last_updated, source_code) FROM stdin;
10754437767114707	/opt/airflow/dags/recommendation_dag.py	2026-09-13 02:28:51.783382+08	from datetime import datetime, timedelta\nfrom airflow import DAG\nfrom airflow.providers.docker.operators.docker import DockerOperator\nfrom docker.types import Mount\n\ndefault_args = {\n    'owner': 'omni-store',\n    'depends_on_past': False,\n    'email_on_failure': False,\n    'email_on_retry': False,\n    'retries': 1,\n    'retry_delay': timedelta(minutes=5),\n}\n\nwith DAG(\n    'spark_recommender_batch',\n    default_args=default_args,\n    description='Run Spark ALS Recommendation Batch Job using Maven',\n    schedule_interval='@daily',  # 每天執行一次\n    start_date=datetime(2023, 1, 1),\n    catchup=False,\n    tags=['spark', 'recommendation'],\n) as dag:\n\n    # 由於我們掛載了 docker.sock，DockerOperator 會在您的宿主機上啟動新容器\n    run_spark_batch = DockerOperator(\n        task_id='run_als_batch_job',\n        image='maven:3.9.6-eclipse-temurin-17',\n        api_version='auto',\n        auto_remove='force',  # 執行完畢後自動刪除暫時的 Maven 容器\n        command='bash -c "mvn compile exec:java -Dmaven.compiler.release=17 -Dexec.mainClass=com.omni.recommender.spark.RecommenderBatchJob"',\n        docker_url='unix://var/run/docker.sock',\n        network_mode='ecommerce_ecommerce-network',  # 讓新容器能夠存取 Redis 和 MinIO\n        environment={\n            'MINIO_ENDPOINT': 'http://ecommerce-minio:9000',\n            'REDIS_HOST': 'ecommerce-redis',\n            'JAVA_TOOL_OPTIONS': '--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED'\n        },\n        mounts=[\n            Mount(\n                source='D:/桌面/MonoRepository/e-commerce-platform',\n                target='/workspace',\n                type='bind'\n            )\n        ],\n        working_dir='/workspace/spark-recommender',\n        mount_tmp_dir=False\n    )\n\n    run_spark_batch\n
\.


--
-- Data for Name: dag_owner_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_owner_attributes (dag_id, owner, link) FROM stdin;
\.


--
-- Data for Name: dag_pickle; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_pickle (id, pickle, created_dttm, pickle_hash) FROM stdin;
\.


--
-- Data for Name: dag_run; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_run (id, dag_id, queued_at, execution_date, start_date, end_date, state, run_id, creating_job_id, external_trigger, run_type, conf, data_interval_start, data_interval_end, last_scheduling_decision, dag_hash, log_template_id, updated_at, clear_number) FROM stdin;
\.


--
-- Data for Name: dag_run_note; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_run_note (user_id, dag_run_id, content, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: dag_schedule_dataset_reference; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_schedule_dataset_reference (dataset_id, dag_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: dag_tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_tag (name, dag_id) FROM stdin;
recommendation	spark_recommender_batch
spark	spark_recommender_batch
\.


--
-- Data for Name: dag_warning; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dag_warning (dag_id, warning_type, message, "timestamp") FROM stdin;
\.


--
-- Data for Name: dagrun_dataset_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dagrun_dataset_event (dag_run_id, event_id) FROM stdin;
\.


--
-- Data for Name: dataset; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dataset (id, uri, extra, created_at, updated_at, is_orphaned) FROM stdin;
\.


--
-- Data for Name: dataset_dag_run_queue; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dataset_dag_run_queue (dataset_id, target_dag_id, created_at) FROM stdin;
\.


--
-- Data for Name: dataset_event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dataset_event (id, dataset_id, extra, source_task_id, source_dag_id, source_run_id, source_map_index, "timestamp") FROM stdin;
\.


--
-- Data for Name: dead_letter_entry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dead_letter_entry (dead_letter_id, cause_message, cause_type, diagnostics, enqueued_at, last_touched, aggregate_identifier, event_identifier, message_type, meta_data, payload, payload_revision, payload_type, sequence_number, time_stamp, token, token_type, type, processing_group, processing_started, sequence_identifier, sequence_index) FROM stdin;
\.


--
-- Data for Name: import_error; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.import_error (id, "timestamp", filename, stacktrace, processor_subdir) FROM stdin;
\.


--
-- Data for Name: job; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.job (id, dag_id, state, job_type, start_date, end_date, latest_heartbeat, executor_class, hostname, unixname) FROM stdin;
1	\N	failed	SchedulerJob	2026-09-13 02:28:49.861336+08	\N	2026-09-13 02:54:41.123645+08	\N	f3ad5029cdbf	airflow
2	\N	running	SchedulerJob	2026-09-13 12:41:00.409587+08	\N	2026-09-13 17:29:16.082254+08	\N	f3ad5029cdbf	airflow
\.


--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.log (id, dttm, dag_id, task_id, map_index, event, execution_date, run_id, owner, owner_display_name, extra) FROM stdin;
1	2026-09-13 02:28:14.692273+08	\N	\N	\N	cli_check	\N	\N	airflow	\N	{"host_name": "7fb2cba409f2", "full_command": "['/home/airflow/.local/bin/airflow', 'db', 'check']"}
2	2026-09-13 02:28:16.462494+08	\N	\N	\N	cli_users_create	\N	\N	airflow	\N	{"host_name": "7fb2cba409f2", "full_command": "['/home/airflow/.local/bin/airflow', 'users', 'create', '--role', 'Admin', '--username', 'admin', '--password', '********', '--email', 'admin@example.com', '--firstname', 'admin', '--lastname', 'admin']"}
3	2026-09-13 02:28:40.558957+08	\N	\N	\N	cli_check	\N	\N	airflow	\N	{"host_name": "f3ad5029cdbf", "full_command": "['/home/airflow/.local/bin/airflow', 'db', 'check']"}
4	2026-09-13 02:28:40.721443+08	\N	\N	\N	cli_check	\N	\N	airflow	\N	{"host_name": "f76cfe980d89", "full_command": "['/home/airflow/.local/bin/airflow', 'db', 'check']"}
5	2026-09-13 02:28:45.381772+08	\N	\N	\N	cli_webserver	\N	\N	airflow	\N	{"host_name": "f76cfe980d89", "full_command": "['/home/airflow/.local/bin/airflow', 'webserver']"}
6	2026-09-13 02:28:48.18172+08	\N	\N	\N	cli_scheduler	\N	\N	airflow	\N	{"host_name": "f3ad5029cdbf", "full_command": "['/home/airflow/.local/bin/airflow', 'scheduler']"}
7	2026-09-13 12:40:51.712209+08	\N	\N	\N	cli_check	\N	\N	airflow	\N	{"host_name": "f3ad5029cdbf", "full_command": "['/home/airflow/.local/bin/airflow', 'db', 'check']"}
8	2026-09-13 12:40:51.711216+08	\N	\N	\N	cli_check	\N	\N	airflow	\N	{"host_name": "f76cfe980d89", "full_command": "['/home/airflow/.local/bin/airflow', 'db', 'check']"}
9	2026-09-13 12:40:56.228516+08	\N	\N	\N	cli_webserver	\N	\N	airflow	\N	{"host_name": "f76cfe980d89", "full_command": "['/home/airflow/.local/bin/airflow', 'webserver']"}
10	2026-09-13 12:40:59.301029+08	\N	\N	\N	cli_scheduler	\N	\N	airflow	\N	{"host_name": "f3ad5029cdbf", "full_command": "['/home/airflow/.local/bin/airflow', 'scheduler']"}
\.


--
-- Data for Name: log_template; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.log_template (id, filename, elasticsearch_id, created_at) FROM stdin;
1	{{ ti.dag_id }}/{{ ti.task_id }}/{{ ts }}/{{ try_number }}.log	{dag_id}-{task_id}-{execution_date}-{try_number}	2026-09-13 02:27:56.848216+08
2	dag_id={{ ti.dag_id }}/run_id={{ ti.run_id }}/task_id={{ ti.task_id }}/{% if ti.map_index >= 0 %}map_index={{ ti.map_index }}/{% endif %}attempt={{ try_number }}.log	{dag_id}-{task_id}-{run_id}-{map_index}-{try_number}	2026-09-13 02:27:56.848225+08
\.


--
-- Data for Name: order_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_view (order_id, amount, status, version) FROM stdin;
16f6b63a-3314-4915-acf0-01b2edcf4975	100.00	NOTIFIED	1
\.


--
-- Data for Name: order_view_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_view_items (order_id, price, product_id, quantity) FROM stdin;
16f6b63a-3314-4915-acf0-01b2edcf4975	100.00	561d3d8b-6a35-4cfe-a340-822f09ce01d5	1
\.


--
-- Data for Name: product_images; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_images (product_id, image_url) FROM stdin;
561d3d8b-6a35-4cfe-a340-822f09ce01d5	http://localhost:9000/products/bb885d01-47d2-423d-afd0-b71581e105ef.png
\.


--
-- Data for Name: product_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_tags (product_id, tag) FROM stdin;
561d3d8b-6a35-4cfe-a340-822f09ce01d5	Culture
561d3d8b-6a35-4cfe-a340-822f09ce01d5	History
561d3d8b-6a35-4cfe-a340-822f09ce01d5	Travel
\.


--
-- Data for Name: product_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_view (product_id, description, name, price, status, stock, sub_type, tenant_id, type, version) FROM stdin;
561d3d8b-6a35-4cfe-a340-822f09ce01d5	走進歷史長河，探訪故宮與長城，感受古都的宏偉與深厚文化底蘊。	北京歷史文化深度遊	1050.00	ACTIVE	100	CROSS_STRAIT_TRAVEL	TTRAVEL	TRAVEL	1
\.


--
-- Data for Name: rendered_task_instance_fields; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rendered_task_instance_fields (dag_id, task_id, run_id, map_index, rendered_fields, k8s_pod_yaml) FROM stdin;
\.


--
-- Data for Name: saga_entry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.saga_entry (saga_id, revision, saga_type, serialized_saga) FROM stdin;
144cca18-f238-482e-9d14-0bbef403b5c3	\N	com.example.demo.application.saga.OrderManagementSaga	24775
\.


--
-- Data for Name: serialized_dag; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.serialized_dag (dag_id, fileloc, fileloc_hash, data, data_compressed, last_updated, dag_hash, processor_subdir) FROM stdin;
spark_recommender_batch	/opt/airflow/dags/recommendation_dag.py	10754437767114707	{"__version": 1, "dag": {"schedule_interval": "@daily", "default_args": {"__var": {"owner": "omni-store", "depends_on_past": false, "email_on_failure": false, "email_on_retry": false, "retries": 1, "retry_delay": {"__var": 300.0, "__type": "timedelta"}}, "__type": "dict"}, "catchup": false, "edge_info": {}, "_task_group": {"_group_id": null, "prefix_group_id": true, "tooltip": "", "ui_color": "CornflowerBlue", "ui_fgcolor": "#000", "children": {"run_als_batch_job": ["operator", "run_als_batch_job"]}, "upstream_group_ids": [], "downstream_group_ids": [], "upstream_task_ids": [], "downstream_task_ids": []}, "fileloc": "/opt/airflow/dags/recommendation_dag.py", "_dag_id": "spark_recommender_batch", "_description": "Run Spark ALS Recommendation Batch Job using Maven", "start_date": 1672531200.0, "timezone": "UTC", "tags": ["spark", "recommendation"], "_processor_dags_folder": "/opt/airflow/dags", "tasks": [{"task_id": "run_als_batch_job", "template_fields": ["image", "command", "environment", "env_file", "container_name"], "email_on_retry": false, "email_on_failure": false, "weight_rule": "downstream", "downstream_task_ids": [], "ui_fgcolor": "#000", "retry_delay": 300.0, "pool": "default_pool", "template_fields_renderers": {"env_file": "yaml"}, "ui_color": "#fff", "on_failure_fail_dagrun": false, "_log_config_logger_name": "airflow.task.operators", "template_ext": [".sh", ".bash", ".env"], "owner": "omni-store", "retries": 1, "is_setup": false, "is_teardown": false, "_task_type": "DockerOperator", "_task_module": "airflow.providers.docker.operators.docker", "_is_empty": false, "image": "maven:3.9.6-eclipse-temurin-17", "command": "bash -c \\"mvn compile exec:java -Dmaven.compiler.release=17 -Dexec.mainClass=com.omni.recommender.spark.RecommenderBatchJob\\"", "environment": {"MINIO_ENDPOINT": "http://ecommerce-minio:9000", "REDIS_HOST": "ecommerce-redis", "JAVA_TOOL_OPTIONS": "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED"}}], "dag_dependencies": [], "params": {}}}	\N	2026-09-13 02:28:51.37405+08	e76d00ad00a4bed76b028cea5f40e078	/opt/airflow/dags
\.


--
-- Data for Name: session; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.session (id, session_id, data, expiry) FROM stdin;
\.


--
-- Data for Name: setting; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.setting (id, active_flag, code, data_type, description, name, priority_no, tenant_id, type, value) FROM stdin;
1	Y	TRAVEL	SYSTEM	旅遊，用來轉換商品種類 TRAVEL 。	旅遊	1	TTRAVEL	PRODUCT_TYPE	旅遊
2	Y	CROSS_STRAIT_TRAVEL	PRODUCT_TYPE	兩岸旅遊	兩岸旅遊	1	TTRAVEL	TRAVEL	兩岸旅遊
3	Y	OVERSEA_TRAVEL	PRODUCT_TYPE	海外旅遊	海外旅遊	2	TTRAVEL	TRAVEL	海外旅遊
4	Y	DOMESTIC_TRAVEL	PRODUCT_TYPE	國內旅遊	國內旅遊	3	TTRAVEL	TRAVEL	國內旅遊
5	Y	AIRPORT_PICKUP	SYSTEM	機場接送服務	機場接送服務	2	TTRAVEL	PRODUCT_TYPE	機場接送服務
7	Y	PICK_UP	PRODUCT_TYPE	機場接送回程（從機場到市區）	機場接送 (回程)	2	TTRAVEL	AIRPORT_PICKUP	機場接送 (回程)
6	Y	DROP_OFF	PRODUCT_TYPE	機場接送去程（從市區到機場）	機場接送(去程)	1	TTRAVEL	AIRPORT_PICKUP	機場接送(去程)
\.


--
-- Data for Name: sla_miss; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sla_miss (task_id, dag_id, execution_date, email_sent, "timestamp", description, notification_sent) FROM stdin;
\.


--
-- Data for Name: slot_pool; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.slot_pool (id, pool, slots, description, include_deferred) FROM stdin;
1	default_pool	128	Default pool	f
\.


--
-- Data for Name: task_fail; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_fail (id, task_id, dag_id, run_id, map_index, start_date, end_date, duration) FROM stdin;
\.


--
-- Data for Name: task_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_instance (task_id, dag_id, run_id, map_index, start_date, end_date, duration, state, try_number, max_tries, hostname, unixname, job_id, pool, pool_slots, queue, priority_weight, operator, custom_operator_name, queued_dttm, queued_by_job_id, pid, executor_config, updated_at, rendered_map_index, external_executor_id, trigger_id, trigger_timeout, next_method, next_kwargs, task_display_name) FROM stdin;
\.


--
-- Data for Name: task_instance_note; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_instance_note (user_id, task_id, dag_id, run_id, map_index, content, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: task_map; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_map (dag_id, task_id, run_id, map_index, length, keys) FROM stdin;
\.


--
-- Data for Name: task_outlet_dataset_reference; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_outlet_dataset_reference (dataset_id, dag_id, task_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: task_reschedule; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_reschedule (id, task_id, dag_id, run_id, map_index, try_number, start_date, end_date, duration, reschedule_date) FROM stdin;
\.


--
-- Data for Name: token_entry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.token_entry (processor_name, segment, owner, "timestamp", token, token_type) FROM stdin;
__config	0	\N	2026-09-13T04:44:32.504Z	24654	org.axonframework.eventhandling.tokenstore.ConfigToken
order-group	0	17716@user	2026-09-13T09:29:18.932Z	24774	org.axonframework.eventhandling.GlobalSequenceTrackingToken
favorite-processor	0	24580@user	2026-09-13T09:29:18.993Z	24778	org.axonframework.eventhandling.GlobalSequenceTrackingToken
order-saga	0	17716@user	2026-09-13T09:29:18.932Z	24773	org.axonframework.eventhandling.GlobalSequenceTrackingToken
product-group	0	24580@user	2026-09-13T09:29:18.977Z	24779	org.axonframework.eventhandling.GlobalSequenceTrackingToken
\.


--
-- Data for Name: trigger; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trigger (id, classpath, kwargs, created_date, triggerer_id) FROM stdin;
\.


--
-- Data for Name: user_favorite_view; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_favorite_view (favorite_id, created_at, product_id, tenant_id, user_id) FROM stdin;
\.


--
-- Data for Name: variable; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.variable (id, key, val, description, is_encrypted) FROM stdin;
\.


--
-- Data for Name: xcom; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.xcom (dag_run_id, task_id, map_index, key, dag_id, run_id, value, "timestamp") FROM stdin;
\.


--
-- Name: ab_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_permission_id_seq', 5, true);


--
-- Name: ab_permission_view_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_permission_view_id_seq', 95, true);


--
-- Name: ab_permission_view_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_permission_view_role_id_seq', 224, true);


--
-- Name: ab_register_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_register_user_id_seq', 1, false);


--
-- Name: ab_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_role_id_seq', 5, true);


--
-- Name: ab_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_user_id_seq', 1, true);


--
-- Name: ab_user_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_user_role_id_seq', 1, true);


--
-- Name: ab_view_menu_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ab_view_menu_id_seq', 54, true);


--
-- Name: association_value_entry_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.association_value_entry_seq', 51, true);


--
-- Name: callback_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.callback_request_id_seq', 1, false);


--
-- Name: connection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.connection_id_seq', 1, false);


--
-- Name: dag_pickle_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dag_pickle_id_seq', 1, false);


--
-- Name: dag_run_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dag_run_id_seq', 1, false);


--
-- Name: dataset_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dataset_event_id_seq', 1, false);


--
-- Name: dataset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dataset_id_seq', 1, false);


--
-- Name: import_error_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.import_error_id_seq', 1, false);


--
-- Name: job_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.job_id_seq', 2, true);


--
-- Name: log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.log_id_seq', 10, true);


--
-- Name: log_template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.log_template_id_seq', 2, true);


--
-- Name: session_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.session_id_seq', 1, false);


--
-- Name: setting_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.setting_id_seq', 7, true);


--
-- Name: slot_pool_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.slot_pool_id_seq', 1, true);


--
-- Name: task_fail_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.task_fail_id_seq', 1, false);


--
-- Name: task_reschedule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.task_reschedule_id_seq', 1, false);


--
-- Name: trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trigger_id_seq', 1, false);


--
-- Name: variable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.variable_id_seq', 1, false);


--
-- Data for Name: BLOBS; Type: BLOBS; Schema: -; Owner: -
--

BEGIN;

SELECT pg_catalog.lo_open('24654', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e746f6b656e73746f72652e436f6e666967546f6b656e222c7b22636f6e666967223a5b226a6176612e7574696c2e436f6c6c656374696f6e732453696e676c65746f6e4d6170222c7b226964223a2233333633383036622d656434352d343538612d613537612d633631333561656634313534227d5d7d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24714', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a307d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24715', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a307d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24716', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a307d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24717', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a307d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24721', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24722', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24723', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24724', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24728', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24729', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24730', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24731', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24732', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24734', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24735', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24737', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a317d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24738', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24740', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24742', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24743', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24744', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24745', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24746', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24747', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24760', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24761', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24762', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24763', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a327d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24764', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a337d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24765', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a337d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24766', 131072);
SELECT pg_catalog.lowrite(0, '\x5b22636f6d2e6578616d706c652e64656d6f2e6170706c69636174696f6e2e736167612e4f726465724d616e6167656d656e7453616761222c7b227061796d656e744964223a6e756c6c2c22616d6f756e74223a5b226a6176612e6d6174682e426967446563696d616c222c3130305d2c226f726465724964223a2231366636623633612d333331342d343931352d616366302d303162326564636634393735222c227061796d656e74436f6d706c65746564223a66616c73652c227061796d656e74446561646c696e654964223a2239306464666138382d613663312d343432612d623561642d396436316166643165333736222c22697343616e63656c6c696e67223a66616c73652c2272657365727665644974656d73223a5b226a6176612e7574696c2e41727261794c697374222c5b5d5d7d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24767', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a337d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24768', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a337d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24769', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a347d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24770', 131072);
SELECT pg_catalog.lowrite(0, '\x5b22636f6d2e6578616d706c652e64656d6f2e6170706c69636174696f6e2e736167612e4f726465724d616e6167656d656e7453616761222c7b227061796d656e744964223a2238376636383731312d313462632d343262622d383731372d633664393562393433306264222c22616d6f756e74223a5b226a6176612e6d6174682e426967446563696d616c222c3130305d2c226f726465724964223a2231366636623633612d333331342d343931352d616366302d303162326564636634393735222c227061796d656e74436f6d706c65746564223a66616c73652c227061796d656e74446561646c696e654964223a2239306464666138382d613663312d343432612d623561642d396436316166643165333736222c22697343616e63656c6c696e67223a66616c73652c2272657365727665644974656d73223a5b226a6176612e7574696c2e41727261794c697374222c5b7b2270726f647563744964223a22494e562d35363164336438622d366133352d346366652d613334302d383232663039636530316435222c227175616e74697479223a312c227072696365223a5b226a6176612e6d6174682e426967446563696d616c222c305d2c22737562746f74616c223a5b226a6176612e6d6174682e426967446563696d616c222c305d7d5d5d7d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24771', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a367d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24772', 131072);
SELECT pg_catalog.lowrite(0, '\x5b22636f6d2e6578616d706c652e64656d6f2e6170706c69636174696f6e2e736167612e4f726465724d616e6167656d656e7453616761222c7b227061796d656e744964223a2238376636383731312d313462632d343262622d383731372d633664393562393433306264222c22616d6f756e74223a5b226a6176612e6d6174682e426967446563696d616c222c3130305d2c226f726465724964223a2231366636623633612d333331342d343931352d616366302d303162326564636634393735222c227061796d656e74436f6d706c65746564223a747275652c227061796d656e74446561646c696e654964223a2239306464666138382d613663312d343432612d623561642d396436316166643165333736222c22697343616e63656c6c696e67223a66616c73652c2272657365727665644974656d73223a5b226a6176612e7574696c2e41727261794c697374222c5b7b2270726f647563744964223a22494e562d35363164336438622d366133352d346366652d613334302d383232663039636530316435222c227175616e74697479223a312c227072696365223a5b226a6176612e6d6174682e426967446563696d616c222c305d2c22737562746f74616c223a5b226a6176612e6d6174682e426967446563696d616c222c305d7d5d5d7d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24773', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a387d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24774', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a387d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24775', 131072);
SELECT pg_catalog.lowrite(0, '\x5b22636f6d2e6578616d706c652e64656d6f2e6170706c69636174696f6e2e736167612e4f726465724d616e6167656d656e7453616761222c7b227061796d656e744964223a2238376636383731312d313462632d343262622d383731372d633664393562393433306264222c22616d6f756e74223a5b226a6176612e6d6174682e426967446563696d616c222c3130305d2c226f726465724964223a2231366636623633612d333331342d343931352d616366302d303162326564636634393735222c227061796d656e74436f6d706c65746564223a747275652c227061796d656e74446561646c696e654964223a2239306464666138382d613663312d343432612d623561642d396436316166643165333736222c22697343616e63656c6c696e67223a66616c73652c2272657365727665644974656d73223a5b226a6176612e7574696c2e41727261794c697374222c5b7b2270726f647563744964223a22494e562d35363164336438622d366133352d346366652d613334302d383232663039636530316435222c227175616e74697479223a312c227072696365223a5b226a6176612e6d6174682e426967446563696d616c222c305d2c22737562746f74616c223a5b226a6176612e6d6174682e426967446563696d616c222c305d7d5d5d7d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24778', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a387d5d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('24779', 131072);
SELECT pg_catalog.lowrite(0, '\x5b226f72672e61786f6e6672616d65776f726b2e6576656e7468616e646c696e672e476c6f62616c53657175656e6365547261636b696e67546f6b656e222c7b22676c6f62616c496e646578223a387d5d');
SELECT pg_catalog.lo_close(0);

COMMIT;

--
-- Name: ab_permission ab_permission_name_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission
    ADD CONSTRAINT ab_permission_name_uq UNIQUE (name);


--
-- Name: ab_permission ab_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission
    ADD CONSTRAINT ab_permission_pkey PRIMARY KEY (id);


--
-- Name: ab_permission_view ab_permission_view_permission_id_view_menu_id_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view
    ADD CONSTRAINT ab_permission_view_permission_id_view_menu_id_uq UNIQUE (permission_id, view_menu_id);


--
-- Name: ab_permission_view ab_permission_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view
    ADD CONSTRAINT ab_permission_view_pkey PRIMARY KEY (id);


--
-- Name: ab_permission_view_role ab_permission_view_role_permission_view_id_role_id_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view_role
    ADD CONSTRAINT ab_permission_view_role_permission_view_id_role_id_uq UNIQUE (permission_view_id, role_id);


--
-- Name: ab_permission_view_role ab_permission_view_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view_role
    ADD CONSTRAINT ab_permission_view_role_pkey PRIMARY KEY (id);


--
-- Name: ab_register_user ab_register_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_register_user
    ADD CONSTRAINT ab_register_user_pkey PRIMARY KEY (id);


--
-- Name: ab_register_user ab_register_user_username_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_register_user
    ADD CONSTRAINT ab_register_user_username_uq UNIQUE (username);


--
-- Name: ab_role ab_role_name_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_role
    ADD CONSTRAINT ab_role_name_uq UNIQUE (name);


--
-- Name: ab_role ab_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_role
    ADD CONSTRAINT ab_role_pkey PRIMARY KEY (id);


--
-- Name: ab_user ab_user_email_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user
    ADD CONSTRAINT ab_user_email_uq UNIQUE (email);


--
-- Name: ab_user ab_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user
    ADD CONSTRAINT ab_user_pkey PRIMARY KEY (id);


--
-- Name: ab_user_role ab_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user_role
    ADD CONSTRAINT ab_user_role_pkey PRIMARY KEY (id);


--
-- Name: ab_user_role ab_user_role_user_id_role_id_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user_role
    ADD CONSTRAINT ab_user_role_user_id_role_id_uq UNIQUE (user_id, role_id);


--
-- Name: ab_user ab_user_username_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user
    ADD CONSTRAINT ab_user_username_uq UNIQUE (username);


--
-- Name: ab_view_menu ab_view_menu_name_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_view_menu
    ADD CONSTRAINT ab_view_menu_name_uq UNIQUE (name);


--
-- Name: ab_view_menu ab_view_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_view_menu
    ADD CONSTRAINT ab_view_menu_pkey PRIMARY KEY (id);


--
-- Name: alembic_version alembic_version_pkc; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.alembic_version
    ADD CONSTRAINT alembic_version_pkc PRIMARY KEY (version_num);


--
-- Name: association_value_entry association_value_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.association_value_entry
    ADD CONSTRAINT association_value_entry_pkey PRIMARY KEY (id);


--
-- Name: callback_request callback_request_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.callback_request
    ADD CONSTRAINT callback_request_pkey PRIMARY KEY (id);


--
-- Name: connection connection_conn_id_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connection
    ADD CONSTRAINT connection_conn_id_uq UNIQUE (conn_id);


--
-- Name: connection connection_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connection
    ADD CONSTRAINT connection_pkey PRIMARY KEY (id);


--
-- Name: dag_code dag_code_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_code
    ADD CONSTRAINT dag_code_pkey PRIMARY KEY (fileloc_hash);


--
-- Name: dag_owner_attributes dag_owner_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_owner_attributes
    ADD CONSTRAINT dag_owner_attributes_pkey PRIMARY KEY (dag_id, owner);


--
-- Name: dag_pickle dag_pickle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_pickle
    ADD CONSTRAINT dag_pickle_pkey PRIMARY KEY (id);


--
-- Name: dag dag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag
    ADD CONSTRAINT dag_pkey PRIMARY KEY (dag_id);


--
-- Name: dag_run dag_run_dag_id_execution_date_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run
    ADD CONSTRAINT dag_run_dag_id_execution_date_key UNIQUE (dag_id, execution_date);


--
-- Name: dag_run dag_run_dag_id_run_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run
    ADD CONSTRAINT dag_run_dag_id_run_id_key UNIQUE (dag_id, run_id);


--
-- Name: dag_run_note dag_run_note_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run_note
    ADD CONSTRAINT dag_run_note_pkey PRIMARY KEY (dag_run_id);


--
-- Name: dag_run dag_run_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run
    ADD CONSTRAINT dag_run_pkey PRIMARY KEY (id);


--
-- Name: dag_tag dag_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_tag
    ADD CONSTRAINT dag_tag_pkey PRIMARY KEY (name, dag_id);


--
-- Name: dag_warning dag_warning_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_warning
    ADD CONSTRAINT dag_warning_pkey PRIMARY KEY (dag_id, warning_type);


--
-- Name: dagrun_dataset_event dagrun_dataset_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dagrun_dataset_event
    ADD CONSTRAINT dagrun_dataset_event_pkey PRIMARY KEY (dag_run_id, event_id);


--
-- Name: dataset_event dataset_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset_event
    ADD CONSTRAINT dataset_event_pkey PRIMARY KEY (id);


--
-- Name: dataset dataset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT dataset_pkey PRIMARY KEY (id);


--
-- Name: dataset_dag_run_queue datasetdagrunqueue_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset_dag_run_queue
    ADD CONSTRAINT datasetdagrunqueue_pkey PRIMARY KEY (dataset_id, target_dag_id);


--
-- Name: dead_letter_entry dead_letter_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dead_letter_entry
    ADD CONSTRAINT dead_letter_entry_pkey PRIMARY KEY (dead_letter_id);


--
-- Name: dag_schedule_dataset_reference dsdr_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_schedule_dataset_reference
    ADD CONSTRAINT dsdr_pkey PRIMARY KEY (dataset_id, dag_id);


--
-- Name: dead_letter_entry idxhlr8io86j74qy298xf720n16v; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dead_letter_entry
    ADD CONSTRAINT idxhlr8io86j74qy298xf720n16v UNIQUE (processing_group, sequence_identifier, sequence_index);


--
-- Name: import_error import_error_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.import_error
    ADD CONSTRAINT import_error_pkey PRIMARY KEY (id);


--
-- Name: job job_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.job
    ADD CONSTRAINT job_pkey PRIMARY KEY (id);


--
-- Name: log log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: log_template log_template_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_template
    ADD CONSTRAINT log_template_pkey PRIMARY KEY (id);


--
-- Name: order_view order_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_view
    ADD CONSTRAINT order_view_pkey PRIMARY KEY (order_id);


--
-- Name: product_view product_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_view
    ADD CONSTRAINT product_view_pkey PRIMARY KEY (product_id);


--
-- Name: rendered_task_instance_fields rendered_task_instance_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rendered_task_instance_fields
    ADD CONSTRAINT rendered_task_instance_fields_pkey PRIMARY KEY (dag_id, task_id, run_id, map_index);


--
-- Name: saga_entry saga_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.saga_entry
    ADD CONSTRAINT saga_entry_pkey PRIMARY KEY (saga_id);


--
-- Name: serialized_dag serialized_dag_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.serialized_dag
    ADD CONSTRAINT serialized_dag_pkey PRIMARY KEY (dag_id);


--
-- Name: session session_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session
    ADD CONSTRAINT session_pkey PRIMARY KEY (id);


--
-- Name: session session_session_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.session
    ADD CONSTRAINT session_session_id_key UNIQUE (session_id);


--
-- Name: setting setting_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.setting
    ADD CONSTRAINT setting_pkey PRIMARY KEY (id);


--
-- Name: sla_miss sla_miss_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sla_miss
    ADD CONSTRAINT sla_miss_pkey PRIMARY KEY (task_id, dag_id, execution_date);


--
-- Name: slot_pool slot_pool_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.slot_pool
    ADD CONSTRAINT slot_pool_pkey PRIMARY KEY (id);


--
-- Name: slot_pool slot_pool_pool_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.slot_pool
    ADD CONSTRAINT slot_pool_pool_uq UNIQUE (pool);


--
-- Name: task_fail task_fail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_fail
    ADD CONSTRAINT task_fail_pkey PRIMARY KEY (id);


--
-- Name: task_instance_note task_instance_note_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance_note
    ADD CONSTRAINT task_instance_note_pkey PRIMARY KEY (task_id, dag_id, run_id, map_index);


--
-- Name: task_instance task_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance
    ADD CONSTRAINT task_instance_pkey PRIMARY KEY (dag_id, task_id, run_id, map_index);


--
-- Name: task_map task_map_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_map
    ADD CONSTRAINT task_map_pkey PRIMARY KEY (dag_id, task_id, run_id, map_index);


--
-- Name: task_reschedule task_reschedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_reschedule
    ADD CONSTRAINT task_reschedule_pkey PRIMARY KEY (id);


--
-- Name: task_outlet_dataset_reference todr_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_outlet_dataset_reference
    ADD CONSTRAINT todr_pkey PRIMARY KEY (dataset_id, dag_id, task_id);


--
-- Name: token_entry token_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.token_entry
    ADD CONSTRAINT token_entry_pkey PRIMARY KEY (processor_name, segment);


--
-- Name: trigger trigger_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trigger
    ADD CONSTRAINT trigger_pkey PRIMARY KEY (id);


--
-- Name: user_favorite_view uk27etb5jtbu9q1jnbqbhno9b94; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_favorite_view
    ADD CONSTRAINT uk27etb5jtbu9q1jnbqbhno9b94 UNIQUE (tenant_id, user_id, product_id);


--
-- Name: user_favorite_view user_favorite_view_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_favorite_view
    ADD CONSTRAINT user_favorite_view_pkey PRIMARY KEY (favorite_id);


--
-- Name: variable variable_key_uq; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.variable
    ADD CONSTRAINT variable_key_uq UNIQUE (key);


--
-- Name: variable variable_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.variable
    ADD CONSTRAINT variable_pkey PRIMARY KEY (id);


--
-- Name: xcom xcom_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcom
    ADD CONSTRAINT xcom_pkey PRIMARY KEY (dag_run_id, task_id, map_index, key);


--
-- Name: dag_id_state; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX dag_id_state ON public.dag_run USING btree (dag_id, state);


--
-- Name: idx_ab_register_user_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_ab_register_user_username ON public.ab_register_user USING btree (lower((username)::text));


--
-- Name: idx_ab_user_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_ab_user_username ON public.ab_user USING btree (lower((username)::text));


--
-- Name: idx_dag_run_dag_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dag_run_dag_id ON public.dag_run USING btree (dag_id);


--
-- Name: idx_dag_run_queued_dags; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dag_run_queued_dags ON public.dag_run USING btree (state, dag_id) WHERE ((state)::text = 'queued'::text);


--
-- Name: idx_dag_run_running_dags; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dag_run_running_dags ON public.dag_run USING btree (state, dag_id) WHERE ((state)::text = 'running'::text);


--
-- Name: idx_dagrun_dataset_events_dag_run_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dagrun_dataset_events_dag_run_id ON public.dagrun_dataset_event USING btree (dag_run_id);


--
-- Name: idx_dagrun_dataset_events_event_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dagrun_dataset_events_event_id ON public.dagrun_dataset_event USING btree (event_id);


--
-- Name: idx_dataset_id_timestamp; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_dataset_id_timestamp ON public.dataset_event USING btree (dataset_id, "timestamp");


--
-- Name: idx_fileloc_hash; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_fileloc_hash ON public.serialized_dag USING btree (fileloc_hash);


--
-- Name: idx_job_dag_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_job_dag_id ON public.job USING btree (dag_id);


--
-- Name: idx_job_state_heartbeat; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_job_state_heartbeat ON public.job USING btree (state, latest_heartbeat);


--
-- Name: idx_log_dag; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_log_dag ON public.log USING btree (dag_id);


--
-- Name: idx_log_dttm; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_log_dttm ON public.log USING btree (dttm);


--
-- Name: idx_log_event; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_log_event ON public.log USING btree (event);


--
-- Name: idx_next_dagrun_create_after; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_next_dagrun_create_after ON public.dag USING btree (next_dagrun_create_after);


--
-- Name: idx_root_dag_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_root_dag_id ON public.dag USING btree (root_dag_id);


--
-- Name: idx_task_fail_task_instance; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_task_fail_task_instance ON public.task_fail USING btree (dag_id, task_id, run_id, map_index);


--
-- Name: idx_task_reschedule_dag_run; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_task_reschedule_dag_run ON public.task_reschedule USING btree (dag_id, run_id);


--
-- Name: idx_task_reschedule_dag_task_run; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_task_reschedule_dag_task_run ON public.task_reschedule USING btree (dag_id, task_id, run_id, map_index);


--
-- Name: idx_uri_unique; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_uri_unique ON public.dataset USING btree (uri);


--
-- Name: idx_xcom_key; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_xcom_key ON public.xcom USING btree (key);


--
-- Name: idx_xcom_task_instance; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_xcom_task_instance ON public.xcom USING btree (dag_id, task_id, run_id, map_index);


--
-- Name: idxe67wcx5fiq9hl4y4qkhlcj9cg; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxe67wcx5fiq9hl4y4qkhlcj9cg ON public.dead_letter_entry USING btree (processing_group);


--
-- Name: idxgv5k1v2mh6frxuy5c0hgbau94; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxgv5k1v2mh6frxuy5c0hgbau94 ON public.association_value_entry USING btree (saga_id, saga_type);


--
-- Name: idxk45eqnxkgd8hpdn6xixn8sgft; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxk45eqnxkgd8hpdn6xixn8sgft ON public.association_value_entry USING btree (saga_type, association_key, association_value);


--
-- Name: idxrwucpgs6sn93ldgoeh2q9k6bn; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxrwucpgs6sn93ldgoeh2q9k6bn ON public.dead_letter_entry USING btree (processing_group, sequence_identifier);


--
-- Name: job_type_heart; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX job_type_heart ON public.job USING btree (job_type, latest_heartbeat);


--
-- Name: sm_dag; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sm_dag ON public.sla_miss USING btree (dag_id);


--
-- Name: ti_dag_run; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_dag_run ON public.task_instance USING btree (dag_id, run_id);


--
-- Name: ti_dag_state; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_dag_state ON public.task_instance USING btree (dag_id, state);


--
-- Name: ti_job_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_job_id ON public.task_instance USING btree (job_id);


--
-- Name: ti_pool; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_pool ON public.task_instance USING btree (pool, state, priority_weight);


--
-- Name: ti_state; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_state ON public.task_instance USING btree (state);


--
-- Name: ti_state_lkp; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_state_lkp ON public.task_instance USING btree (dag_id, task_id, run_id, state);


--
-- Name: ti_trigger_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ti_trigger_id ON public.task_instance USING btree (trigger_id);


--
-- Name: ab_permission_view ab_permission_view_permission_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view
    ADD CONSTRAINT ab_permission_view_permission_id_fkey FOREIGN KEY (permission_id) REFERENCES public.ab_permission(id);


--
-- Name: ab_permission_view_role ab_permission_view_role_permission_view_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view_role
    ADD CONSTRAINT ab_permission_view_role_permission_view_id_fkey FOREIGN KEY (permission_view_id) REFERENCES public.ab_permission_view(id);


--
-- Name: ab_permission_view_role ab_permission_view_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view_role
    ADD CONSTRAINT ab_permission_view_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.ab_role(id);


--
-- Name: ab_permission_view ab_permission_view_view_menu_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_permission_view
    ADD CONSTRAINT ab_permission_view_view_menu_id_fkey FOREIGN KEY (view_menu_id) REFERENCES public.ab_view_menu(id);


--
-- Name: ab_user ab_user_changed_by_fk_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user
    ADD CONSTRAINT ab_user_changed_by_fk_fkey FOREIGN KEY (changed_by_fk) REFERENCES public.ab_user(id);


--
-- Name: ab_user ab_user_created_by_fk_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user
    ADD CONSTRAINT ab_user_created_by_fk_fkey FOREIGN KEY (created_by_fk) REFERENCES public.ab_user(id);


--
-- Name: ab_user_role ab_user_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user_role
    ADD CONSTRAINT ab_user_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.ab_role(id);


--
-- Name: ab_user_role ab_user_role_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ab_user_role
    ADD CONSTRAINT ab_user_role_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.ab_user(id);


--
-- Name: dag_owner_attributes dag.dag_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_owner_attributes
    ADD CONSTRAINT "dag.dag_id" FOREIGN KEY (dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: dag_run_note dag_run_note_dr_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run_note
    ADD CONSTRAINT dag_run_note_dr_fkey FOREIGN KEY (dag_run_id) REFERENCES public.dag_run(id) ON DELETE CASCADE;


--
-- Name: dag_run_note dag_run_note_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run_note
    ADD CONSTRAINT dag_run_note_user_fkey FOREIGN KEY (user_id) REFERENCES public.ab_user(id);


--
-- Name: dag_tag dag_tag_dag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_tag
    ADD CONSTRAINT dag_tag_dag_id_fkey FOREIGN KEY (dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: dagrun_dataset_event dagrun_dataset_event_dag_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dagrun_dataset_event
    ADD CONSTRAINT dagrun_dataset_event_dag_run_id_fkey FOREIGN KEY (dag_run_id) REFERENCES public.dag_run(id) ON DELETE CASCADE;


--
-- Name: dagrun_dataset_event dagrun_dataset_event_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dagrun_dataset_event
    ADD CONSTRAINT dagrun_dataset_event_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.dataset_event(id) ON DELETE CASCADE;


--
-- Name: dag_warning dcw_dag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_warning
    ADD CONSTRAINT dcw_dag_id_fkey FOREIGN KEY (dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: dataset_dag_run_queue ddrq_dag_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset_dag_run_queue
    ADD CONSTRAINT ddrq_dag_fkey FOREIGN KEY (target_dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: dataset_dag_run_queue ddrq_dataset_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dataset_dag_run_queue
    ADD CONSTRAINT ddrq_dataset_fkey FOREIGN KEY (dataset_id) REFERENCES public.dataset(id) ON DELETE CASCADE;


--
-- Name: dag_schedule_dataset_reference dsdr_dag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_schedule_dataset_reference
    ADD CONSTRAINT dsdr_dag_id_fkey FOREIGN KEY (dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: dag_schedule_dataset_reference dsdr_dataset_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_schedule_dataset_reference
    ADD CONSTRAINT dsdr_dataset_fkey FOREIGN KEY (dataset_id) REFERENCES public.dataset(id) ON DELETE CASCADE;


--
-- Name: order_view_items fk1njj7lp9b7x580eufav7nnv8q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_view_items
    ADD CONSTRAINT fk1njj7lp9b7x580eufav7nnv8q FOREIGN KEY (order_id) REFERENCES public.order_view(order_id);


--
-- Name: product_tags fkojq5yfornykjymku58on8uijb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_tags
    ADD CONSTRAINT fkojq5yfornykjymku58on8uijb FOREIGN KEY (product_id) REFERENCES public.product_view(product_id);


--
-- Name: product_images fkq3snvq9x410py756cbjmh7gw4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_images
    ADD CONSTRAINT fkq3snvq9x410py756cbjmh7gw4 FOREIGN KEY (product_id) REFERENCES public.product_view(product_id);


--
-- Name: rendered_task_instance_fields rtif_ti_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rendered_task_instance_fields
    ADD CONSTRAINT rtif_ti_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON DELETE CASCADE;


--
-- Name: task_fail task_fail_ti_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_fail
    ADD CONSTRAINT task_fail_ti_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON DELETE CASCADE;


--
-- Name: task_instance task_instance_dag_run_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance
    ADD CONSTRAINT task_instance_dag_run_fkey FOREIGN KEY (dag_id, run_id) REFERENCES public.dag_run(dag_id, run_id) ON DELETE CASCADE;


--
-- Name: dag_run task_instance_log_template_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dag_run
    ADD CONSTRAINT task_instance_log_template_id_fkey FOREIGN KEY (log_template_id) REFERENCES public.log_template(id);


--
-- Name: task_instance_note task_instance_note_ti_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance_note
    ADD CONSTRAINT task_instance_note_ti_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON DELETE CASCADE;


--
-- Name: task_instance_note task_instance_note_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance_note
    ADD CONSTRAINT task_instance_note_user_fkey FOREIGN KEY (user_id) REFERENCES public.ab_user(id);


--
-- Name: task_instance task_instance_trigger_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_instance
    ADD CONSTRAINT task_instance_trigger_id_fkey FOREIGN KEY (trigger_id) REFERENCES public.trigger(id) ON DELETE CASCADE;


--
-- Name: task_map task_map_task_instance_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_map
    ADD CONSTRAINT task_map_task_instance_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: task_reschedule task_reschedule_dr_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_reschedule
    ADD CONSTRAINT task_reschedule_dr_fkey FOREIGN KEY (dag_id, run_id) REFERENCES public.dag_run(dag_id, run_id) ON DELETE CASCADE;


--
-- Name: task_reschedule task_reschedule_ti_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_reschedule
    ADD CONSTRAINT task_reschedule_ti_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON DELETE CASCADE;


--
-- Name: task_outlet_dataset_reference todr_dag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_outlet_dataset_reference
    ADD CONSTRAINT todr_dag_id_fkey FOREIGN KEY (dag_id) REFERENCES public.dag(dag_id) ON DELETE CASCADE;


--
-- Name: task_outlet_dataset_reference todr_dataset_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task_outlet_dataset_reference
    ADD CONSTRAINT todr_dataset_fkey FOREIGN KEY (dataset_id) REFERENCES public.dataset(id) ON DELETE CASCADE;


--
-- Name: xcom xcom_task_instance_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcom
    ADD CONSTRAINT xcom_task_instance_fkey FOREIGN KEY (dag_id, task_id, run_id, map_index) REFERENCES public.task_instance(dag_id, task_id, run_id, map_index) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict jEfl4vzywgbcTtldDFU1GtTecGeFXVmHQJfIEVXZAcjuTkTrcMSvxGGTrPuAShu

