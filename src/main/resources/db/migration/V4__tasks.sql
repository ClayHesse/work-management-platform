CREATE TABLE tasks (
    id UUID not null primary key,
    title varchar(200) not null,
    description TEXT,
    project_id UUID not null,
    assigned_to UUID,
    created_by UUID not null,
    status varchar(20) not null check (status IN ('CREATED','ASSIGNED','IN_PROGRESS','COMPLETED')),
    priority varchar(50) not null DEFAULT 'MEDIUM' check (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    created_at TIMESTAMP WITH TIME ZONE not null DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_tasks_assigned FOREIGN KEY (assigned_to) REFERENCES users(id),
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assigned ON tasks(assigned_to);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assigned_status ON tasks(assigned_to, status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_unassigned ON tasks(created_at) WHERE assigned_to IS NULL;