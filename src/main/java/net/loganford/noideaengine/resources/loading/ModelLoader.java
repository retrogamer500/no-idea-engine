package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Window;
import net.loganford.noideaengine.config.json.ModelConfig;
import net.loganford.noideaengine.graphics.*;
import net.loganford.noideaengine.utils.file.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ModelLoader extends ResourceLoader {

    private List<ModelConfig> modelsToLoad;
    private boolean loadMaterial;

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    public ModelLoader(Game game) {
        this(game, true);
    }

    public ModelLoader(Game game, boolean loadMaterial) {
        super(game);
        this.loadMaterial = loadMaterial;
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getModelManager().unloadGroups(ctx);
        modelsToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getModels() != null) {
            modelsToLoad.addAll(game.getConfig().getResources().getModels()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        ModelConfig config = modelsToLoad.remove(0);
        Model model = load(game, config);
        populateResource(model, config);
        log.info("Model loaded. Name: " + config.getKey() + ".");
        game.getModelManager().put(config.getKey(), model);
    }

    @Override
    public int getRemaining() {
        return modelsToLoad.size();
    }

    public Model load(Game game, ModelConfig modelConfig) {
        File file = new File(modelConfig.getFilename());

        DataSource location = modelConfig.getResourceMapper().get(modelConfig.getFilename());
        String locationName = location.toString();
        String[] split = locationName.split("\\.");
        String phint = split[split.length-1];

        AIScene aiScene = Assimp.aiImportFileFromMemory(location.loadBytes(), Assimp.aiProcessPreset_TargetRealtime_MaxQuality |
                Assimp.aiProcess_Triangulate |
                Assimp.aiProcess_JoinIdenticalVertices |
                Assimp.aiProcess_GenNormals |
                Assimp.aiProcess_GenUVCoords
        , phint);

        boolean swapZY = modelConfig.isSwapZY();
        float scale = modelConfig.getScale();

        //Import meshes
        Model model = new Model();
        int meshNum = aiScene.mNumMeshes();
        PointerBuffer meshBuffer=aiScene.mMeshes();
        PointerBuffer materialsBuffer=aiScene.mMaterials();

        //Todo: materials: https://github.com/johnnic431/Terminus-Engine/blob/master/src/com/form2bgames/terminusengine/model/AssimpModelWrapper.java
        //https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html

        for(int i=0; i < meshNum; i++) {
            Mesh mesh = new Mesh();
            mesh.setMaterial(getDefaultMaterial());

            AIMesh aiMesh = AIMesh.create(meshBuffer.get(i));

            //Handle materials
            AIMaterial aiMaterial = AIMaterial.create(materialsBuffer.get(aiMesh.mMaterialIndex()));
            AIString path = AIString.calloc();
            Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
            String data = path.dataString();
            path.free();

            if(StringUtils.isNotBlank(data)) {
                String textureKey = modelConfig.getImagePrefix() + data + modelConfig.getImageSuffix();
                Texture texture = game.getTextureManager().get(textureKey);
                mesh.getMaterial().setDiffuse(texture);
            }


            AIVector3D.Buffer normalBuffer = aiMesh.mNormals();
            List<Vector3f> points = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Vector2f> uvCoords = new ArrayList<>();
            for(int j=0; j < aiMesh.mNumVertices(); j++){
                AIVector3D aiVertex = aiMesh.mVertices().get(j);

                Vector3f point = new Vector3f(aiVertex.x() * scale, aiVertex.y() * scale, aiVertex.z() * scale);
                if(swapZY) {
                    float temp = point.z;
                    point.z = point.y;
                    point.y = temp;
                    point.x = -point.x;
                }
                points.add(point);

                if(normalBuffer != null) {
                    Vector3f normal = new Vector3f(normalBuffer.get(j).x(), normalBuffer.get(j).y(), normalBuffer.get(j).z());

                    if(swapZY) {
                        float temp = normal.z;
                        normal.z = normal.y;
                        normal.y = temp;
                        normal.x = -normal.x;
                    }

                    normals.add(normal);
                }
                else {
                    log.warn("Found point with no normals!");
                    normals.add(new Vector3f(0, 0, 0));
                }

                Vector2f uvCoord;
                if(aiMesh.mTextureCoords(0) != null) {
                    uvCoord = new Vector2f(
                            aiMesh.mTextureCoords(0).get(j).x(),
                            aiMesh.mTextureCoords(0).get(j).y());
                }
                else {
                    uvCoord = new Vector2f(0, 0);
                }

                uvCoords.add(uvCoord);
            }
            mesh.setPoints(points);
            mesh.setNormals(normals);
            mesh.setUvCoordinates(uvCoords);

            //Vertex faces
            List<Face> faces = new ArrayList<>();
            for(int j=0; j < aiMesh.mNumFaces(); j++){
                AIFace aiFace = aiMesh.mFaces().get(j);

                if(aiFace.mNumIndices() != 3) {
                    log.warn("Found face with " + aiFace.mNumIndices() + " points!");
                    continue;
                }

                Face face = new Face();

                //Position
                Vector3f[] facePoints = new Vector3f[3];
                facePoints[0] = points.get(aiFace.mIndices().get(0));
                facePoints[1] = points.get(aiFace.mIndices().get(1));
                facePoints[2] = points.get(aiFace.mIndices().get(2));
                face.setPositions(facePoints);

                //Normal
                Vector3f[] faceNormals = new Vector3f[3];

                if(modelConfig.isGenerateNormals()) {
                    Vector3f side01 = V3F_2;
                    Vector3f side02 = V3F_3;

                    side01.set(facePoints[1]).sub(facePoints[0]);
                    side02.set(facePoints[2]).sub(facePoints[0]);
                    Vector3f crossed = V3F.set(side01.cross(side02).normalize());

                    faceNormals[0] = new Vector3f(crossed);
                    faceNormals[1] = new Vector3f(crossed);
                    faceNormals[2] = new Vector3f(crossed);
                }
                else {
                    faceNormals[0] = normals.get(aiFace.mIndices().get(0));
                    faceNormals[1] = normals.get(aiFace.mIndices().get(1));
                    faceNormals[2] = normals.get(aiFace.mIndices().get(2));
                }
                face.setNormals(faceNormals);

                //UV Coords
                Vector2f[] faceUvs = new Vector2f[3];
                faceUvs[0] = uvCoords.get(aiFace.mIndices().get(0));
                faceUvs[1] = uvCoords.get(aiFace.mIndices().get(1));
                faceUvs[2] = uvCoords.get(aiFace.mIndices().get(2));
                face.setUvs(faceUvs);

                //Todo: colors, other attributes

                faces.add(face);
            }
            mesh.setFaces(faces);
            model.getMeshes().add(mesh);
        }

        Assimp.aiReleaseImport(aiScene);

        //Init model if this is not a unit test
        if(Window.initialized) {
            model.init();
        }

        return model;
    }

    public Model load(float[] vertices, float[] uv, float[] normals) {
        Model model = new Model();
        Mesh mesh = new Mesh();
        int faceNum = vertices.length / 9;

        for (int i = 0; i < faceNum; i++) {
            Face face = new Face();

            for (int j = 0; j < 3; j++) {
                face.getPositions()[j] = new Vector3f(vertices[9 * i + 3 * j], vertices[9 * i + 3 * j + 1],
                        vertices[9 * i + 3 * j + 2]);
                face.getNormals()[j] = new Vector3f(normals[9 * i + 3 * j], normals[9 * i + 3 * j + 1],
                        normals[9 * i + 3 * j + 2]);
                face.getUvs()[j] = new Vector2f(uv[6 * i + 2 * j], uv[6 * i + 2 * j + 1]);
            }

            mesh.getPoints().add(face.getPositions()[0]);
            mesh.getPoints().add(face.getPositions()[1]);
            mesh.getPoints().add(face.getPositions()[2]);
            mesh.getNormals().add(face.getNormals()[0]);
            mesh.getNormals().add(face.getNormals()[1]);
            mesh.getNormals().add(face.getNormals()[2]);
            mesh.getUvCoordinates().add(face.getUvs()[0]);
            mesh.getUvCoordinates().add(face.getUvs()[1]);
            mesh.getUvCoordinates().add(face.getUvs()[2]);

            mesh.getFaces().add(face);
        }

        if(loadMaterial) {
            mesh.setMaterial(getDefaultMaterial());
        }

        model.getMeshes().add(mesh);
        model.init();

        return model;
    }

    private Material getDefaultMaterial() {
        Material material = new Material();
        material.setDiffuse(getGame().getRenderer().getTextureWhite());
        return material;
    }
}
