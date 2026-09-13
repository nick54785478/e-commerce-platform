import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductQueriedView } from '../../../products/models/product.model';

import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { RadioButtonModule } from 'primeng/radiobutton';

@Component({
  selector: 'app-stock-adjustment-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DialogModule, ButtonModule, InputNumberModule, RadioButtonModule],
  templateUrl: './stock-adjustment-modal.component.html',
  styleUrls: ['./stock-adjustment-modal.component.css']
})
export class StockAdjustmentModalComponent implements OnInit {

  isVisible = true;

  @Input() product!: ProductQueriedView;

  @Output() close = new EventEmitter<any>();

  form!: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      action: ['ADD', Validators.required],
      quantity: [null, [Validators.required, Validators.min(1)]]
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const formValue = this.form.getRawValue();

    // Check if reduce quantity is more than current stock
    if (formValue.action === 'REDUCE' && formValue.quantity > this.product.stock) {
      alert('Cannot reduce stock below 0.');
      this.isSubmitting = false;
      return;
    }

    this.close.emit({
      action: formValue.action,
      quantity: formValue.quantity
    });
  }

  onCancel(): void {
    this.close.emit(false);
  }
}
