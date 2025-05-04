-- Create Draws table
CREATE TABLE draws (
    id UUID PRIMARY KEY,
    lottery_type VARCHAR(50) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Create Draw Results table
CREATE TABLE draw_results (
    id UUID PRIMARY KEY,
    draw_id UUID NOT NULL REFERENCES draws(id),
    winning_combination VARCHAR(255) NOT NULL,
    result_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_draw_result_draw FOREIGN KEY (draw_id) REFERENCES draws(id)
);

-- Create Tickets table
CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    draw_id UUID NOT NULL,
    numbers VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ticket_draw FOREIGN KEY (draw_id) REFERENCES draws(id)
);

-- Create Invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    ticket_data TEXT NOT NULL,
    register_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount DOUBLE PRECISION NOT NULL
);

-- Create Payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- Create indexes for better performance
CREATE INDEX idx_draws_status ON draws(status);
CREATE INDEX idx_draws_start_time ON draws(start_time);
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_draw_id ON tickets(draw_id);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_draw_results_draw_id ON draw_results(draw_id); 