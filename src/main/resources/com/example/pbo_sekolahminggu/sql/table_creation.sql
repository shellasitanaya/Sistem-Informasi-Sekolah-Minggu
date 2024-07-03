-- QUERY DATABASE SEKOLAH MINGGU
-- GURU
CREATE TABLE tbl_guru (
    id     SERIAL CONSTRAINT guru_pk PRIMARY KEY,
    nama   VARCHAR(50) CONSTRAINT guru_nama_nn NOT NULL,
    nip         VARCHAR(20) CONSTRAINT guru_nip_nn NOT NULL,
    no_telp     VARCHAR(20)  CONSTRAINT guru_no_telp_nn NOT NULL,
    alamat      VARCHAR(255)  CONSTRAINT guru_alamat_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
	CONSTRAINT guru_nip_u UNIQUE (nip)
);

-- KELAS
CREATE TABLE tbl_kelas (
    id SERIAL CONSTRAINT kelas_id_pk PRIMARY KEY,
    nama_kelas VARCHAR(50) CONSTRAINT kelas_nama_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
	CONSTRAINT nama_kelas_u UNIQUE (nama_kelas)
);

-- TAHUN AJARAN
CREATE TABLE tbl_tahun_ajaran (
    id SERIAL CONSTRAINT tahun_ajaran_pk PRIMARY KEY,
    tahun_ajaran    VARCHAR(10) CONSTRAINT tahun_ajaran_nn NOT NULL,
    status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT tahun_ajaran_u UNIQUE (tahun_ajaran)
);

-- ANAK
CREATE TABLE tbl_anak (
    id SERIAL CONSTRAINT anak_pk PRIMARY KEY,
    nis VARCHAR(20) CONSTRAINT anak_nis_nn NOT NULL,
    nama VARCHAR(50) CONSTRAINT anak_nama_anak_nn NOT NULL,
    jenis_kelamin VARCHAR(6) CONSTRAINT jenis_kelamin_nn NOT NULL CHECK (jenis_kelamin IN ('female', 'male')),
    status_aktif INT NOT NULL DEFAULT 1,
    nama_ortu VARCHAR(100) CONSTRAINT namaOrtu_anak_nn NOT NULL,
    alamat_ortu VARCHAR(100) CONSTRAINT alamatOrtu_anak_nn NOT NULL,
    no_telp_ortu VARCHAR(15) CONSTRAINT noTelpOrtu_anak_nn NOT NULL,
    CONSTRAINT nis_nama_u UNIQUE (nis)
);

-- KEBAKTIAN
CREATE TABLE tbl_kebaktian (
    id      SERIAL CONSTRAINT kebaktian_pk PRIMARY KEY,
    jenis_kebaktian   VARCHAR(50) CONSTRAINT kebaktian_jenis_nn NOT NULL,
    tanggal           DATE CONSTRAINT kebaktian_tanggal_nn NOT NULL,
	status_aktif INT NOT NULL DEFAULT 1,
	CONSTRAINT tgl_jenis_kebaktian_u UNIQUE (jenis_kebaktian, tanggal)
);

-- KELAS PER TAHUN
CREATE TABLE tbl_kelas_per_tahun (
    id SERIAL CONSTRAINT kelas_per_tahun_pk PRIMARY KEY,
    id_kelas           INT CONSTRAINT kelas_id_fk_nn NOT NULL,
    id_tahun_ajaran    INT CONSTRAINT tahun_ajaran_fk_nn NOT NULL,
    ruang_kelas        VARCHAR(50) CONSTRAINT kelas_ruang_nn NOT NULL,
    kelas_paralel      VARCHAR(50) DEFAULT 'A' CONSTRAINT kelas_paralel_nn NOT NULL ,
	status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kelas_per_tahun_kelas_fk FOREIGN KEY (id_kelas) REFERENCES tbl_kelas (id) ON DELETE CASCADE,
    CONSTRAINT kelas_per_tahun_tahun_ajaran_fk FOREIGN KEY (id_tahun_ajaran) REFERENCES tbl_tahun_ajaran (id) ON DELETE CASCADE,
	CONSTRAINT kelas_tahun_ruang_u UNIQUE (id_kelas, id_tahun_ajaran, kelas_paralel)
);

-- HISTORI MENGAJAR
CREATE TABLE tbl_histori_mengajar (
    id SERIAL CONSTRAINT histori_mengajar_pk PRIMARY KEY,
    id_guru INT CONSTRAINT histori_guru_fk_nn NOT NULL,
    id_kelas_per_tahun INT CONSTRAINT histori_kelas_fk_nn NOT NULL,
	status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT histori_mengajar_guru_fk FOREIGN KEY (id_guru) REFERENCES tbl_guru (id) ON DELETE CASCADE,
    CONSTRAINT histori_mengajar_tahun_ajaran_fk FOREIGN KEY (id_kelas_per_tahun) REFERENCES tbl_kelas_per_tahun (id) ON DELETE CASCADE,
	CONSTRAINT histori_mengajar_guru_kelas_u UNIQUE (id_guru, id_kelas_per_tahun)
);

-- KEHADIRAN GURU
CREATE TABLE tbl_kehadiran_guru (
    id SERIAL CONSTRAINT kehadiran_guru_pk PRIMARY KEY,
    id_histori_mengajar INT CONSTRAINT kehadiran_guru_fk_nn NOT NULL,
    id_kebaktian INT CONSTRAINT kehadiran_kebaktian_fk_nn NOT NULL,
    presensi BOOLEAN DEFAULT false CONSTRAINT guru_presensi_nn NOT NULL,
	status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT kehadiran_guru_guru_fk FOREIGN KEY (id_histori_mengajar) REFERENCES tbl_histori_mengajar (id) ON DELETE CASCADE,
 	CONSTRAINT kehadiran_guru_kebaktian_fk FOREIGN KEY (id_kebaktian) REFERENCES tbl_kebaktian (id) ON DELETE CASCADE,
	CONSTRAINT kehadiran_guru_histori_kebaktian_fk UNIQUE (id_histori_mengajar, id_kebaktian)
);

--HISTORI KELAS ANAK
CREATE TABLE tbl_histori_kelas_anak (
    id SERIAL CONSTRAINT histori_kelas_anak_pk PRIMARY KEY,
    id_anak INT CONSTRAINT histori_anak_fk_nn NOT NULL,
    id_kelas_per_tahun INT CONSTRAINT histori_kelas_fk_nn NOT NULL,
	status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT histori_kelas_anak_anak_fk FOREIGN KEY (id_anak) REFERENCES tbl_anak (id) ON DELETE CASCADE,
    CONSTRAINT histori_kelas_anak_kelas_fk FOREIGN KEY (id_kelas_per_tahun) REFERENCES tbl_kelas_per_tahun (id) ON DELETE CASCADE,
	CONSTRAINT histori_anak_kelas_u UNIQUE (id_anak, id_kelas_per_tahun)
);


-- KEHADIRAN ANAK
CREATE TABLE tbl_kehadiran_anak (
    id SERIAL CONSTRAINT kehadiran_anak_pk PRIMARY KEY,
    id_histori_kelas_anak INT CONSTRAINT kehadiran_histori_anak_fk_nn NOT NULL,
    id_kebaktian INT CONSTRAINT kehadiran_kebaktian_fk_nn NOT NULL,
    presensi     BOOLEAN DEFAULT false CONSTRAINT anak_presensi_nn NOT NULL,
	status_aktif INT NOT NULL DEFAULT 1,
    CONSTRAINT histori_kelas_kehadiran_fk FOREIGN KEY (id_histori_kelas_anak) REFERENCES tbl_histori_kelas_anak (id) ON DELETE CASCADE,
    CONSTRAINT kebaktian_kehadiran_fk FOREIGN KEY (id_kebaktian) REFERENCES tbl_kebaktian (id) ON DELETE CASCADE,
	CONSTRAINT histori_kelas_kebaktian_u UNIQUE (id_histori_kelas_anak, id_kebaktian)
);

--end

