CREATE TABLE projects (
    id UUID not null primary key,
    title varchar(200) not null,
    description TEXT,
    created_by UUID not null,
    assigned_team UUID,
    status varchar(20) not null check (status IN ('ACTIVE','COMPLETED','ARCHIVED')),
    created_at TIMESTAMP WITH TIME ZONE not null DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_owner_user FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_assigned_team FOREIGN KEY (assigned_team) REFERENCES teams(id)
);

CREATE INDEX idx_project_team ON projects(assigned_team);
CREATE INDEX idx_project_created_by ON projects(created_by);
CREATE INDEX idx_project_unassigned ON projects(created_at) WHERE assigned_team IS NULL;