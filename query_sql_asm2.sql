
create table users(
	id int not null primary key auto_increment,
    email varchar(255) not null unique,
    password varchar(255) not null,
	status int not null,
	full_name varchar(255) not null,
	description varchar(255),
	address varchar(255),
	phone_number varchar(255),
	image varchar(255),
	role_id int not null,
	cv_id int,
    foreign key (role_id) references roles(id),
    foreign key (cv_id) references cvs(id)
    );
insert into users values(1, "khaihoan@gmail.com", "$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu", 1, "khai hoan", "i am cr7", "song binh", "123123123", "image", 1, null); 

create table cvs(
	id int not null primary key auto_increment,
    file_name varchar(255));

create table roles(
	id int not null primary key auto_increment,
    role_name varchar(255) not null);
insert into roles values (1, "ROLE_CANDIDATE"), (2, "ROLE_EMPLOYER");

create table companies(
	id int not null primary key auto_increment,
    name varchar(255),
    address varchar(255),
    email varchar(255),
    phone_number varchar(255),
    description varchar(255) ,
    logo varchar(255),
    status int not null,
    user_id int ,
    foreign key (user_id) references users(id));

create table categories(
	id int primary key auto_increment,
    name varchar(255),
    number_choose int);
insert into categories values (1, "Node Js", 2), (2, "Spring boot", 3), (3, "React", 1);

create table recruitments(
	id int not null primary key auto_increment,
    title varchar(255) not null,
    description varchar(255) not null,
    experience varchar(255) not null,
    quantity int not null,
    address varchar(255) not null,
    deadline date not null,
    salary varchar(255) not null,
    type varchar(255) not null,
    r_rank varchar(255),
    status int not null,
    view int,
    created_at datetime not null,
    category_id int not null,
    company_id int not null,
    foreign key (category_id) references categories(id),
    foreign key (company_id) references companies(id));

create table apply_post(
	id int not null primary key auto_increment,
    name_cv varchar(255),
    text varchar(255),
    status int not null,
    created_at datetime not null,
    recruitment_id int not null,
    user_id int not null,
    foreign key (recruitment_id) references recruitments(id),
    foreign key (user_id) references users(id));
    
create table follow_company(
	id int not null primary key auto_increment,
    company_id int not null,
    user_id int not null,
    foreign key (company_id) references companies(id),
    foreign key (user_id) references users(id));
    
create table save_job(
	id int not null primary key auto_increment,
    recruitment_id int not null,
    user_id int not null,
    foreign key (recruitment_id) references recruitments(id),
    foreign key (user_id) references users(id));

create table verification_token(
	id int primary key auto_increment,
    token varchar(255),
    user_id int,
    expiry_date datetime,
    foreign key (user_id) references users(id));