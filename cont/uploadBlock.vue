<template>
  <div class="tile__row--paddings tile--background-white"
       v-preloader="{ showLoader, text: $t('events.loading')}">

    <div class="tile__row tile__row--column-direction tile__row--align-start">
      <span class="tile__text tile__text--medium18Bold">{{$t('contract.upload')}}</span>
      <span class="tile__text tile__text--small12 tile__row--margin-top-half">{{$t('contract.uploadDescription')}}</span>
    </div>

    <template v-if="!isFO">
      <div v-for="type in allScanDocumentTypes.LEGAL" class="reset-img tile__row tile__row--margin-top-half">
        <input v-if="uploadInputs[type.toLowerCase() + 'Input']" :ref="type.toLowerCase() + 'file_input'"
               type="file" accept="application/pdf, image/jpeg" @change="uploadFile($event, type)">

        <div class="tile__row tile__row--background tile__row--column-direction tile__row--flex-center tile__row--paddings-half"
             :ref="type.toLowerCase()" @click="openImageDocument(type)">

          <span :for="type.toLowerCase()"
                class="tile__text tile__text--small14 tile__row--self-start">
            {{$t('contract.' + type.toLowerCase())}}
          </span>
          <span v-if="uploadFilenames[type.toLowerCase() + 'Filename'].origin !== ''" class="tile__text tile__text--small12 tile__row--self-start"
                :ref="type.toLowerCase() + `file_name`">
              {{uploadFilenames[type.toLowerCase() + 'Filename'].origin}}
            </span>
        </div>

        <div v-if="!isModeration" class="tile__row--background tile__row--paddings-half" :ref="type.toLowerCase() + 'Icon'" @click="deleteFile(type.toLowerCase())">
          <svg-icon :name="uploadIcons[type.toLowerCase() + 'Icon']"></svg-icon>
        </div>

        <div v-else class="tile__row--background tile__row--paddings-half" :ref="type.toLowerCase() + 'Icon'">
          <svg-icon :name="uploadIcons[type.toLowerCase() + 'Icon']"></svg-icon>
        </div>

      </div>
    </template>

    <template v-else>
      <div v-for="type in allScanDocumentTypes.INDIVIDUAL" class="reset-img tile__row tile__row--margin-top-half">
        <input :id="type.toLowerCase()"  :ref="type.toLowerCase() + 'file_input'"
               type="file" accept="application/pdf, image/jpeg" @change="uploadFile($event, type)">

        <div class="tile__row tile__row--background tile__row--column-direction tile__row--flex-center tile__row--paddings-half"
             :ref="type.toLowerCase()" @click="openImageDocument(type)">

          <span :for="type.toLowerCase()"
                class="tile__text tile__text--small14 tile__row--self-start">
            {{$t('contract.' + type.toLowerCase())}}
          </span>
          <span v-if="uploadFilenames[type.toLowerCase() + 'Filename'].origin !== ''" class="tile__text tile__text--small12 tile__row--self-start"
                :ref="type.toLowerCase() + `file_name`">
              {{uploadFilenames[type.toLowerCase() + 'Filename'].origin}}
            </span>
        </div>

        <div class="tile__row--background tile__row--paddings-half" :ref="type.toLowerCase() + 'Icon'" @click="deleteFile(type.toLowerCase())">
          <svg-icon :name="uploadIcons[type.toLowerCase() + 'Icon']"></svg-icon>
        </div>

      </div>
    </template>

    <image-component
      v-if="isImageOpen"
      :file="image"
      :title="imageTitle"
      :path="imagePath"
      :type="imageType"
      :image-ref="imageRef"
      @confirm="setModerateFlag"
      @reject=""
      @close="closeImage"
      @delete="deleteFile"
    />
  </div>
</template>

<script>
    import {scanDocumentType, allScanDocumentTypes, setModerateImageFlag} from "@/api/contract";
    import preloader from "../../../directive/preloader/index.js";
    import imageComponent from "../../contract/ImageComponent";

    export default {
        name: "uploadBlock",
        directives: {preloader},
        components:{imageComponent},
        data() {
            return {
                image:{},
                uploadImages:[],
                checkedDocs:[],
                showLoader: false,
                isImageOpen: false,
                isModeration: false,
                // isModeration: true,
                uploadInputs: {
                    edrpoInput: true,
                    ndsInput: true,
                    single_taxInput: true,
                    bank_detailsInput: true,
                    statute_first_pageInput: true,
                    statute_second_pageInput: true,
                    statute_last_pageInput: true,
                    appointment_orderInput: true,
                    power_of_attorneyInput: true,
                    passport_first_pageInput: true,
                    passport_second_pageInput: true,
                    passport_registrationInput: true,
                    innInput: true,
                },
                uploadIcons: {
                    edrpoIcon: 'upload',
                    ndsIcon: 'upload',
                    single_taxIcon: 'upload',
                    bank_detailsIcon: 'upload',
                    statute_first_pageIcon: 'upload',
                    statute_second_pageIcon: 'upload',
                    statute_last_pageIcon: 'upload',
                    appointment_orderIcon: 'upload',
                    power_of_attorneyIcon: 'upload',
                    passport_first_pageIcon: 'upload',
                    passport_second_pageIcon: 'upload',
                    passport_registrationIcon: 'upload',
                    innIcon: 'upload',
                },
                uploadFilenames:{
                    edrpoFilename:{
                        origin:'',
                        scan:''
                    },
                    ndsFilename: {
                        origin:'',
                        scan:''
                    },
                    single_taxFilename: {
                        origin:'',
                        scan:''
                    },
                    bank_detailsFilename: {
                        origin:'',
                        scan:''
                    },
                    statute_first_pageFilename: {
                        origin:'',
                        scan:''
                    },
                    statute_second_pageFilename: {
                        origin:'',
                        scan:''
                    },
                    statute_last_pageFilename: {
                        origin:'',
                        scan:''
                    },
                    appointment_orderFilename: {
                        origin:'',
                        scan:''
                    },
                    power_of_attorneyFilename: {
                        origin:'',
                        scan:''
                    },
                    passport_first_pageFilename: {
                        origin:'',
                        scan:''
                    },
                    passport_second_pageFilename: {
                        origin:'',
                        scan:''
                    },
                    passport_registrationFilename: {
                        origin:'',
                        scan:''
                    },
                    innFilename: {
                        origin:'',
                        scan:''
                    },
                },
                imageTitle: '',
                imagePath: '',
                imageType:'',
                imageRef:'',
                scanDocumentType,
                allScanDocumentTypes
            }
        },
        props: {
            id: {
                type: String,
                required: true
            },
            isFO: {
                type: Boolean,
                required: true
            },
            images: {
                type: Array,
                required: true
            }
        },
        mounted(){
            let that = this;
            that.isModeration = that.$route.params.isModeration;
            that.checkedDocs = that.images.filter(it => it.is_moderate).map(it => it.type)
        },
        watch: {
            images() {
                if(this.images.length !== 0){
                    this.setUnsetUploads();
                }
            },
            isFO(){
                this.$nextTick(() => {
                    if(this.isFO){
                        this.allScanDocumentTypes.INDIVIDUAL.forEach(type => {
                            this.setUploadButtonDefault(type.toLowerCase())
                        });
                    } else {
                        this.allScanDocumentTypes.LEGAL.forEach(type => {
                            this.setUploadButtonDefault(type.toLowerCase())
                        });
                    }
                    this.setUnsetUploads();
                });
            }
        },
        methods: {
            setModerateFlag(type, isModerate){
                setModerateImageFlag(this.id, type, isModerate).then(response => {
                    console.log("Successfully set moderation flag");
                    if(isModerate){
                        this.setUploadCheckedButton(type.toLowerCase())
                    } else {
                        // this.unsetUploadButton(type.toLowerCase())
                    }
                })
            },
            closeImage(){
                let that = this;
                that.isImageOpen = false;
            },
            deleteFile(type){
                let that = this;
                let currentName = that.uploadFilenames[type + 'Filename'].scan;
                that.uploadFilenames[type + 'Filename'] = {origin: '', scan: ''};
                that.setUploadButtonDefault(type);
                that.closeImage();
                that.$emit('remove', currentName, type.toUpperCase());
            },
            openImageDocument(type){
                let that = this;
                that.fillImageComponent(type);
                that.isImageOpen = true;
            },

            fillImageComponent(type){
                let that = this;
                let file = that.uploadImages.filter(image => image.name === that.uploadFilenames[type.toLowerCase() + 'Filename'].origin)[0];

                if(typeof file !== 'undefined'){
                    that.image = file
                } else {
                    let file = that.images.filter(image => image.name === that.uploadFilenames[type.toLowerCase() + 'Filename'].origin)[0];
                    that.image = new File([], typeof  file !== 'undefined' ? file.name : 'filename')
                }

                that.imageTitle = that.$t('contract.' + type.toLowerCase());
                that.imagePath = typeof that.images.filter(image => image.type === type)[0] !== 'undefined' ? that.images.filter(image => image.type === type)[0].path : '';
                that.imageType = type;
                that.imageRef = type.toLowerCase();
            },

            uploadFile(event, type) {

                let that = this;

                let reader = new FileReader();

                if (event.target.files && event.target.files[0]) {
                    that.uploadImages.push(event.target.files[0]);
                    let idxDot = event.target.files[0].name.lastIndexOf(".") + 1;
                    let extFile = event.target.files[0].name.substr(idxDot, event.target.files[0].length).toLowerCase();
                    if (extFile === "jpg" || extFile === "jpeg" || extFile === "pdf") {
                        let file_size = (event.target.files[0].size / 1024 / 1024);// in MB

                        if (file_size > 5) {
                            alert(this.$t('errors.image-size'));
                        } else {
                            reader.readAsDataURL(event.target.files[0]);
                            reader.onload = function (e) {
                                let fileBlob = that.dataURItoBlob(reader.result);

                                let fileName = type + '.' + extFile;
                                let fileOfBlob = new File([fileBlob], fileName);

                                that.uploadFilenames[type.toLowerCase() + 'Filename'].origin = event.target.files[0].name;
                                that.uploadFilenames[type.toLowerCase() + 'Filename'].scan = fileName;
                                that.setUploadButton(type.toLowerCase());

                                that.$emit('add-image', {file: fileOfBlob, fileName: event.target.files[0].name});
                            };
                        }
                    } else {
                        alert(this.$t('errors.image-format-contract'));
                    }
                }
            },

            dataURItoBlob(dataURI) {
                let byteString = atob(dataURI.split(',')[1]);
                let mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

                let ab = new ArrayBuffer(byteString.length);
                let dw = new DataView(ab);

                for (let i = 0; i < byteString.length; i++) {
                    dw.setUint8(i, byteString.charCodeAt(i));
                }

                return new Blob([ab], {type: mimeString});
            },

            setUnsetUploads() {
                let that = this;
                let type;

                that.images.forEach(item => {
                    that.uploadFilenames[item.type.toLowerCase() + 'Filename'].origin = item.name;

                    type = that.scanDocumentType[item.type].toLowerCase();

                    if(item.is_moderate && that.isModeration){
                        that.setUploadCheckedButton(type);
                    } else {
                        that.setUploadButton(type);
                    }
                });
            },

            setUploadButtonDefault(ref) {
                let that = this;
                let button = that.$refs[ref][0];
                let buttonIcon = that.$refs[ref + 'Icon'][0];

                if (typeof button !== 'undefined') {
                    button.classList.remove("buttonUpload");
                    button.classList.remove("buttonUploadChecked");

                    buttonIcon.classList.remove("buttonUploadChecked");
                    buttonIcon.classList.remove("buttonUploadDelete");

                    that.uploadInputs[ref + 'Input'] = true;
                }

                that.setIcon(ref, 'upload');
            },

            setUploadButton(ref) {
                let that = this;

                let button = that.$refs[ref][0];
                let buttonIcon = that.$refs[ref + 'Icon'][0];

                if(typeof button !== 'undefined' && typeof buttonIcon !== 'undefined'){
                    buttonIcon.classList.remove("buttonUploadChecked");

                    if(!that.isModeration){

                        buttonIcon.classList.add("buttonUploadDelete");
                        that.setIcon(ref, 'trash-can');
                    } else {
                        buttonIcon.classList.add("buttonUpload");
                        that.setIcon(ref, '');
                    }

                    button.classList.add("buttonUpload");

                    that.uploadInputs[ref + 'Input'] = false;
                }
            },

            setUploadCheckedButton(type) {
                let that = this;
                let button = that.$refs[type][0];
                let buttonIcon = that.$refs[type + 'Icon'][0];

                button.classList.remove("buttonUpload");
                button.classList.add("buttonUploadChecked");

                buttonIcon.classList.remove("buttonUpload");
                buttonIcon.classList.add("buttonUploadChecked");

                that.setIcon(type, 'done_tiny');

                let currentImageTypes = that.images.map(it => it.type);
                that.checkedDocs.push(type.toUpperCase());
                let typeForFill = that.allScanDocumentTypes.LEGAL.filter(it => !that.checkedDocs.includes(it) && currentImageTypes.includes(it))[0];

                that.fillImageComponent(typeForFill);
            },

            setIcon(ref, iconName) {
                let that = this;
                that.uploadIcons[ref + 'Icon'] = iconName;
            },

            unsetUploadButton(ref) {
                let that = this;
                let button = this.$refs[ref];
                if (typeof button !== 'undefined') {
                    button.classList.remove("buttonUpload");
                    button.classList.remove("buttonUploadChecked");
                    button.classList.add("scanCopyFail");
                }
                that.setIcon(ref, 'upload');
            },

            // addDocumentType(type) {
            //     this.$emit('add-document-type', type)
            // },
            clearInput(ref) {
                this.$refs['file_input' + ref].value = "";
            }
        }
    }
</script>

<style lang="scss" scoped>

  @import '../../../assets/styles/global/variables';

  .reset-img {
    flex-shrink: 0;
    align-items: stretch;
    position: relative;
    color: #6d757a;
    cursor: pointer;
    overflow: hidden;
  }

  .reset-img input,
  input::-webkit-file-upload-button{
    position: absolute;
    width: 100%;
    height: 100%;
    right: 0;
    top: 0;
    opacity: 0;
    cursor: pointer;
    z-index: 3;
  }

  .svg-icon {
    width: 18px;
    height: 18px;
    fill: #636E69;
    flex-shrink: 0;
    margin-left: auto;
  }

  .margin-top {
    margin-top: calc(var(--indent) / 2);
  }

  .buttonUpload {
    background-color: $ui_green_20;
  }

  .buttonUploadChecked {
    background-color: $ui_green_50;
  }

  .buttonUploadDelete {
    background-color: $ui_red;
  }

</style>
