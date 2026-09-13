import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { InputTextModule } from 'primeng/inputtext';
import { SettingService } from '../../services/setting.service';
import { SettingGottenView, YesNo } from '../../models/setting.model';
import { SettingFormModalComponent } from '../setting-form-modal/setting-form-modal.component';
import { DATA_TYPE_OPTIONS } from '../../constants/setting.constants';

@Component({
  selector: 'app-setting-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    SelectModule,
    ButtonModule,
    TagModule,
    TooltipModule,
    InputTextModule,
    SettingFormModalComponent
  ],
  templateUrl: './setting-list.component.html',
  styleUrls: ['./setting-list.component.css']
})
export class SettingListComponent implements OnInit {
  
  settings: SettingGottenView[] = [];
  loading = false;
  
  dataTypes: any[] = DATA_TYPE_OPTIONS;
  types: any[] = [];
  
  activeFlagOptions = [
    { label: 'Active', value: 'Y' },
    { label: 'Inactive', value: 'N' }
  ];
  
  selectedDataType: string | undefined;
  selectedType: string | undefined;
  selectedName: string | undefined;
  selectedActiveFlag: string | undefined;

  showModal = false;
  selectedSetting: SettingGottenView | null = null;

  constructor(private settingService: SettingService) {}

  ngOnInit(): void {
    // 預設不查出資料，讓使用者自行輸入條件後查詢
  }

  onSearch(): void {
    this.loadSettings();
  }

  onDataTypeChange(): void {
    this.selectedType = undefined;
    this.types = [];
    
    if (this.selectedDataType) {
      this.settingService.getSettings('TTRAVEL', this.selectedDataType).subscribe({
        next: (data) => {
          const uniqueTypes = [...new Set(data.map(item => item.type))];
          this.types = uniqueTypes.map(t => ({ label: t, value: t }));
        },
        error: (err) => console.error('Failed to load types for DataType', err)
      });
    }
  }

  loadSettings(): void {
    this.loading = true;
    this.settingService.getSettings('TTRAVEL', this.selectedDataType, this.selectedType, this.selectedName, this.selectedActiveFlag).subscribe({
      next: (data) => {
        this.settings = data;
        this.extractFilters(data);
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load settings', err);
        this.loading = false;
      }
    });
  }

  extractFilters(data: SettingGottenView[]): void {
    // dataTypes are now statically defined from DATA_TYPE_OPTIONS
    
    const uniqueTypes = [...new Set(data.map(item => item.type))];
    this.types = uniqueTypes.map(t => ({ label: t, value: t }));
  }

  clearFilter(): void {
    this.selectedDataType = undefined;
    this.selectedType = undefined;
    this.selectedName = undefined;
    this.selectedActiveFlag = undefined;
    this.settings = []; // 清空資料表
  }

  openCreateModal(): void {
    this.selectedSetting = null;
    this.showModal = true;
  }

  openEditModal(setting: SettingGottenView): void {
    this.selectedSetting = setting;
    this.showModal = true;
  }

  closeModal(result: any): void {
    this.showModal = false;
    this.selectedSetting = null;
    if (result) {
      this.loadSettings();
    }
  }

  deleteSetting(setting: SettingGottenView): void {
    if (confirm(`Are you sure you want to delete ${setting.name}?`)) {
      this.settingService.deleteSetting(setting.id).subscribe({
        next: () => {
          this.loadSettings();
        },
        error: (err) => {
          alert('Delete failed: ' + (err.error?.message || err.message));
        }
      });
    }
  }
}
