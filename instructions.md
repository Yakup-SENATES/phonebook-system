# Backend (Java) Assessment

## Merhaba

Bu deðerlendirme iþe baþvuru sürecindeki adaylar için hazýrlanmýþ olup, katýlacak kimselerin yaklaþým ve yetkinliklerini deðerlendirmede bizlere yardýmcý olmak için tasarlanmýþtýr.

Deðerlendirme dahilinde; belirtilen süre içerisinde aþaðýda kapsamý ve detaylarý belirlenmiþ projeyi tamamlamanýzý beklemekteyiz. Dikkat edebileceðiniz bir diðer husus ise, bizlerin doðru bir deðerlendirme yapmamýza yardýmcý olacak þekilde iletebileceðiniz en iyi çalýþmayý bizlere teslim ediyor olmanýz.


### Senaryo

Birbirleri ile haberleþen minimum iki microservice'in olduðu bir yapý tasarlayarak, basit bir telefon rehberi uygulamasý oluþturulmasý saðlanacaktýr.

Beklenen iþlevler:
- Rehberde kiþi oluþturma
- Rehberde kiþi kaldýrma
- Rehberdeki kiþiye iletiþim bilgisi ekleme
- Rehberdeki kiþiden iletiþim bilgisi kaldýrma
- Rehberdeki kiþilerin listelenmesi
- Rehberdeki bir kiþiyle ilgili iletiþim bilgilerinin de yer aldýðý detay bilgilerin getirilmesi
- Rehberdeki kiþilerin bulunduklarý konuma göre istatistiklerini çýkartan bir rapor talebi
- Sistemin oluþturduðu raporlarýn listelenmesi
- Sistemin oluþturduðu bir raporun detay bilgilerinin getirilmesi


### Teknik Tasarým

**Kiþiler:**
Sistemde teorik anlamda sýnýrsýz sayýda kiþi kaydý yapýlabilecektir. Her kiþiye baðlý iletiþim bilgileri de yine sýnýrsýz bir biçimde eklenebilmelidir.

Karþýlanmasý beklenen veri yapýsýndaki gerekli alanlar aþaðýdaki gibidir:

- UUID
- Ad
- Soyad
- Firma
- Ýletiþim Bilgisi
    - Bilgi Tipi: Telefon Numarasý, E-mail Adresi, Konum
    - Bilgi Ýçeriði

**Rapor:**
Rapor talepleri asenkron çalýþacaktýr. Kullanýcý bir rapor talep ettiðinde, sistem arkaplanda bu çalýþmayý darboðaz yaratmadan sýralý bir biçimde ele alacak; rapor tamamlandýðýnda ise kullanýcýnýn "raporlarýn listelendiði" endpoint üzerinden raporun durumunu "tamamlandý" olarak gözlemleyebilmesi gerekmektedir.

Rapor basitçe aþaðýdaki bilgileri içerecektir:

- Konum Bilgisi
- O konumda yer alan rehbere kayýtlý kiþi sayýsý
- O konumda yer alan rehbere kayýtlý telefon numarasý sayýsý

Veri yapýsý olarak da:

- UUID
- Raporun Talep Edildiði Tarih
- Rapor Durumu (Hazýrlanýyor, Tamamlandý)

**NOT:** her kiþi eklendiðinde asenkron olarak bir t_report_status tablosuna uuid ve kiþi statusu eklenecek.
kiþi ilk eklendiðinde PENDING status ile eklenecek. daha sonra rapor oluþtur end pointi çalýþtýrýldýðýnda status pending olanlar
kafkaya id leri ile gönderilip ordan rapor servisinde rapor oluþturulup sonucunda da t_report_status tablosunda ilgili uuid li kayýt 
statusu CREATED olarak güncellenecektir.

**NOT:** Deðerlendirme ile ilgili beklentiler için *Teknik Beklentiler* bölümünü dikkatli okuyunuz.


### Teknik Beklentiler

- Kullanýlacak Teknolojiler:
    - Jdk 1.8/17
    - Spring Boot 3.x
    - Maven
    - Git
    - Postgres veya MongoDB
    - Kafka v.b. Message Queue sistemi

- Kýsýtlamalar ve Gereksinimler:
    - Projenin sýk commitlerle Git üzerinde geliþtirilmesi
    - Git üzerinde master, development branchleri ve sürüm taglemelerinin kullanýmý
    - Minimum %60 unit testing code coverage
    - Projenin veritabanýný oluþturacak migration yapýsýnýn oluþturulmuþ olmasý
    - Projenin nasýl çalýþtýrýlacaðýna dair README.md dokümantasyonu
    - Servislerin HTTP üzerinden REST veya GraphQL protokolleri üzerinden iletiþimi saðlanmalý
    - Rapor kýsmýndaki asenkron yapýnýn saðlanmasý için message queue gibi sistemler kullanýlmalýdýr


### Çalýþmanýn Tamamlanmasý

Çalýþma tamamlandýðýnda bu codebase'i kiþisel git repository'sine aktarmanýz, çalýþma bitiminde de bu repository adresiyle paylaþmanýz beklenecektir.


## Sorularýnýz

Deðerlendirmelerle ilgili sorularýnýzý [github@setur.com.tr](mailto:github@setur.com.tr) adresine iletebilirsiniz.


### Lisans

[Apache 2.0](LICENSE) ile lisanslanmýþtýr.