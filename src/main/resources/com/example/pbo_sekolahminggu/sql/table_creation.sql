
-- QUERY DATABASE SEKOLAH MINGGU
begin;
rollback;
-- GURU
CREATE TABLE tbl_guru (
    id     SERIAL CONSTRAINT guru_id_nn NOT NULL,
    nama   VARCHAR(50) CONSTRAINT guru_nama_nn NOT NULL,
    nip         VARCHAR(20) CONSTRAINT guru_nip_nn NOT NULL,
    no_telp     VARCHAR(20)  CONSTRAINT guru_no_telp_nn NOT NULL,
    alamat      VARCHAR(255)  CONSTRAINT guru_alamat_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
	CONSTRAINT guru_id_pk PRIMARY KEY (id),
	CONSTRAINT guru_nip_u UNIQUE (nip)
);

-- KELAS
CREATE TABLE tbl_kelas (
    id  SERIAL CONSTRAINT kelas_id_nn NOT NULL,
    nama_kelas  VARCHAR(50) CONSTRAINT kelas_nama_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kelas_id_pk PRIMARY KEY (id),
 	CONSTRAINT kelas_idd_u UNIQUE (id)
);


-- TAHUN AJARAN
CREATE TABLE tbl_tahun_ajaran (
    id SERIAL CONSTRAINT kelas_id_nn NOT NULL,
    tahun_ajaran    VARCHAR(10) CONSTRAINT tahun_ajaran_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT tahun_ajaran_id_pk PRIMARY KEY (id),
	CONSTRAINT tahun_ajaran_id_u UNIQUE (id)
);

-- ANAK
CREATE TABLE tbl_anak (
    id SERIAL CONSTRAINT kelas_id_nn NOT NULL,
    nis VARCHAR(20) CONSTRAINT anak_nis_nn NOT NULL,
    nama VARCHAR(50) CONSTRAINT anak_nama_anak_nn NOT NULL,
    jenis_kelamin VARCHAR(6) CHECK (jenis_kelamin IN ('female', 'male')),
    status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT anak_id_pk PRIMARY KEY (id),
    CONSTRAINT anak_id_u UNIQUE (id),
    CONSTRAINT nis_u UNIQUE (nis)
);

-- AlTER ANAK
ALTER TABLE tbl_anak
ADD COLUMN nama_ortu VARCHAR(50),
ADD COLUMN alamat_ortu TEXT,
ADD COLUMN no_telp_ortu VARCHAR(20);


-- KEBAKTIAN
CREATE TABLE tbl_kebaktian (
    id      SERIAL CONSTRAINT kebaktian_id_nn NOT NULL,
    jenis_kebaktian   VARCHAR(50) CONSTRAINT kebaktian_jenis_nn NOT NULL,
    tanggal           DATE CONSTRAINT kebaktian_tanggal_nn NOT NULL,
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kebaktian_id_pk PRIMARY KEY (id),
	CONSTRAINT kebaktian_id_u UNIQUE (id)
);


-- KELAS PER TAHUN
CREATE TABLE tbl_kelas_per_tahun (
    id SERIAL CONSTRAINT kelas_per_tahun_pk_nn NOT NULL,
    id_kelas           INT,
    id_tahun_ajaran    INT,
    ruang_kelas        VARCHAR(50) CONSTRAINT kelas_ruang_nn NOT NULL,
    kelas_paralel      VARCHAR(50),
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kelas_per_tahun_pk PRIMARY KEY (id),
	CONSTRAINT kelas_per_tahun_u UNIQUE (id),
    CONSTRAINT kelas_per_tahun_kelas_fk FOREIGN KEY (id_kelas) REFERENCES tbl_kelas (id) ON DELETE SET NULL,
    CONSTRAINT kelas_per_tahun_tahun_ajaran_fk FOREIGN KEY (id_tahun_ajaran) REFERENCES tbl_tahun_ajaran (id) ON DELETE SET NULL
);


-- HISTORI MENGAJAR
CREATE TABLE tbl_histori_mengajar (
    id 		 SERIAL PRIMARY KEY,
    id_guru INT,
    id_kelas_per_tahun INT,
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT histori_mengajar_guru_fk FOREIGN KEY (id_guru) REFERENCES tbl_guru (id) ON DELETE SET NULL,
    CONSTRAINT histori_mengajar_tahun_ajaran_fk FOREIGN KEY (id_kelas_per_tahun) REFERENCES tbl_kelas_per_tahun (id) ON DELETE SET NULL
);

-- KEHADIRAN GURU
CREATE TABLE tbl_kehadiran_guru (
    id  SERIAL CONSTRAINT guru_kehadiran_nn NOT NULL,
    id_histori_mengajar INT,
    Id_kebaktian INT,
    presensi          BOOLEAN DEFAULT false,
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kehadiran_guru_pk PRIMARY KEY (id),
    CONSTRAINT kehadiran_guru_guru_fk FOREIGN KEY (id_histori_mengajar) REFERENCES tbl_histori_mengajar (id),
 CONSTRAINT kehadiran_guru_kebaktian_fk FOREIGN KEY (id_kebaktian) REFERENCES tbl_kebaktian (id)

);

-- HISTORI KELAS ANAK
CREATE TABLE tbl_histori_kelas_anak (
    id SERIAL CONSTRAINT kelas_anak_histori_nn NOT NULL,
    id_anak               INT,
    id_kelas_per_tahun    INT,
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT histori_kelas_anak_pk PRIMARY KEY (id),
    CONSTRAINT histori_kelas_anak_anak_fk FOREIGN KEY (id_anak) REFERENCES tbl_anak (id) ON DELETE SET NULL,
    CONSTRAINT histori_kelas_anak_kelas_fk FOREIGN KEY (id_kelas_per_tahun) REFERENCES tbl_kelas_per_tahun (id) ON DELETE SET NULL
);

-- KEHADIRAN ANAK
CREATE TABLE tbl_kehadiran_anak (
    id SERIAL CONSTRAINT anak_kehadiran_nn NOT NULL,
    id_histori_kelas_anak           INT,
    Id_kebaktian INT,
    presensi         BOOLEAN DEFAULT false,
status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kehadiran_anak_pk PRIMARY KEY (id),
    CONSTRAINT histori_kelas_kehadiran_fk FOREIGN KEY (id_histori_kelas_anak) REFERENCES tbl_histori_kelas_anak (id) ON DELETE SET NULL,
    CONSTRAINT kebaktian_kehadiran_fk FOREIGN KEY (id_kebaktian) REFERENCES tbl_kebaktian (id) ON DELETE SET NULL
);



