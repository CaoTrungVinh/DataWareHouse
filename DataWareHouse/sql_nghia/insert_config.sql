use control_data;

UPDATE config
SET
extension_file='xlsx',
file_name='sinhvien_sang_nhom1.xlsx',
variabless ='STT,Ma_so_sinh_vien,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,Dien_thoai,Email,Que_quan,Ghi_chu',
number_cols='11',
datawarehouse_table ='nhom_1_sinhvien'
WHERE ID=1;

-- nhom 2
UPDATE config
SET
extension_file='xlsx',
file_name='sinhvien_sang_nhom2.xlsx',
variabless ='STT,Ma_sinh_vien,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,DT_lien_lac,Email,Que_quan,Ghi_chu',
number_cols='11',
datawarehouse_table ='nhom_2_sinhvien'
WHERE ID=2;

-- nhom 3
UPDATE config
SET
extension_file='xlsx',
file_name='SinhVien_Sang_Nhom3.xlsx',
variabless ='STT,Ma_sinh_vien,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,DT_lien_lac,Email,Que_quan,Ghi_chu',
number_cols='11',
datawarehouse_table ='nhom_3_sinhvien'
WHERE ID=3;
-- nhom 4
UPDATE config
SET
extension_file='xlsx',
file_name='sinhvien_sang_nhom4.xlsx',
variabless ='STT,ID,FIRST_NAME,LASTNAME,GENDER,BIRTHDAY,ADDRESS',
number_cols='7',
datawarehouse_table ='nhom_4_sinhvien'
WHERE ID=4;

-- nhom 5
