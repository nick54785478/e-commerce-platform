import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { InputNumberModule } from 'primeng/inputnumber';
import { SettingService } from '../../services/setting.service';
import { SettingGottenView, YesNo } from '../../models/setting.model';
import { DATA_TYPE_OPTIONS } from '../../constants/setting.constants';

@Component({
  selector: 'app-setting-form-modal',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    SelectModule,
    AutoCompleteModule,
    InputNumberModule
  ],
  templateUrl: './setting-form-modal.component.html',
  styleUrls: ['./setting-form-modal.component.css']
})
export class SettingFormModalComponent implements OnInit {

  @Input() setting: SettingGottenView | null = null;
  @Output() close = new EventEmitter<boolean>();

  settingForm!: FormGroup;
  isEditMode = false;
  submitting = false;

  dataTypeOptions = DATA_TYPE_OPTIONS;

  statusOptions = [
    { label: 'Active', value: YesNo.Y },
    { label: 'Inactive', value: YesNo.N }
  ];

  types: string[] = [];
  filteredTypes: string[] = [];

  constructor(
    private fb: FormBuilder,
    private settingService: SettingService
  ) {}

  ngOnInit(): void {
    this.isEditMode = !!this.setting;
    this.initForm();
    
    if (this.setting?.dataType) {
      this.loadTypesForDataType(this.setting.dataType);
    }
  }

  initForm(): void {
    this.settingForm = this.fb.group({
      tenantId: [this.setting?.tenantId || 'TTRAVEL', Validators.required],
      dataType: [this.setting?.dataType || '', Validators.required],
      type: [this.setting?.type || '', Validators.required],
      name: [this.setting?.name || '', Validators.required],
      code: [this.setting?.code || '', Validators.required],
      value: [this.setting?.value || ''],
      description: [this.setting?.description || ''],
      priorityNo: [this.setting?.priorityNo || 1, Validators.required],
      activeFlag: [this.setting?.activeFlag || YesNo.Y, Validators.required]
    });
  }

  onDataTypeChange(event: any): void {
    const dataType = event.value;
    // Clear current type selection when Data Type changes
    this.settingForm.patchValue({ type: '' });
    this.loadTypesForDataType(dataType);
  }

  loadTypesForDataType(dataType: string): void {
    if (!dataType) {
      this.types = [];
      return;
    }

    this.settingService.getSettings('TTRAVEL', dataType).subscribe({
      next: (data) => {
        this.types = [...new Set(data.map(item => item.type))];
      },
      error: (err) => console.error('Failed to load types for DataType', err)
    });
  }

  filterTypes(event: any): void {
    const query = event.query.toLowerCase();
    this.filteredTypes = this.types.filter(type => type.toLowerCase().includes(query));
  }

  closeModal(): void {
    this.close.emit(false);
  }

  onSubmit(): void {
    if (this.settingForm.invalid) {
      this.settingForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const formValue = this.settingForm.value;

    if (this.isEditMode && this.setting) {
      this.settingService.updateSetting(this.setting.id, formValue).subscribe({
        next: () => {
          this.submitting = false;
          this.close.emit(true);
        },
        error: (err) => {
          alert('Update failed: ' + (err.error?.message || err.message));
          this.submitting = false;
        }
      });
    } else {
      this.settingService.createSetting(formValue).subscribe({
        next: () => {
          this.submitting = false;
          this.close.emit(true);
        },
        error: (err) => {
          alert('Create failed: ' + (err.error?.message || err.message));
          this.submitting = false;
        }
      });
    }
  }
}
