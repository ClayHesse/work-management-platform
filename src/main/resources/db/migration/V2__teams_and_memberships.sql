CREATE TABLE teams (
    id UUID not null primary key,
    name varchar(100) not null,
    created_at TIMESTAMP WITH TIME ZONE not null DEFAULT now()
);

CREATE TABLE team_memberships (
    id UUID not null primary key,
    team_id UUID not null,
    user_id UUID not null,
    user_role varchar(20) not null check (user_role IN ('LEAD','MEMBER')),

    CONSTRAINT fk_membership_team foreign key (team_id) REFERENCES teams(id),
    CONSTRAINT fk_membership_user foreign key (user_id) REFERENCES users(id),
    CONSTRAINT unique_team_user unique (team_id,user_id)
);

CREATE UNIQUE INDEX one_lead_per_team ON team_memberships(team_id) WHERE user_role = 'LEAD';